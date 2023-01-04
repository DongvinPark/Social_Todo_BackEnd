package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.publictodo.PublicTodoCreateRequest;
import com.example.socialtodobackend.dto.publictodo.PublicTodoDto;
import com.example.socialtodobackend.dto.publictodo.PublicTodoUpdateRequest;
import com.example.socialtodobackend.entity.PublicTodoEntity;
import com.example.socialtodobackend.entity.SupportNagNumberEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.PublicTodoRepository;
import com.example.socialtodobackend.repository.PublicTodoSupportNagNumberRepository;
import com.example.socialtodobackend.repository.UserRepository;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicTodoService {

    private final PublicTodoRepository publicTodoRepository;
    private final UserRepository userRepository;
    private final PublicTodoSupportNagNumberRepository publicTodoSupportNagNumberRepository;


    /**
     * 특정 유저가 작성한 모든 공개 투두 아이템들을 읽어들인다.
     * */
    @Transactional
    public List<PublicTodoDto> getAllPublicTodo(Long authorUserPKId) {
        if(!userRepository.findById(authorUserPKId).isPresent()){
            throw new SocialTodoException(ErrorCode.USER_NOT_FOUND);
        }

        List<PublicTodoDto> publicTodoDtoList = new ArrayList<>();
        for(PublicTodoEntity publicTodoEntity : publicTodoRepository.findAllByAuthorUserId(authorUserPKId)){
            publicTodoDtoList.add(
                PublicTodoDto.fromEntity(publicTodoEntity)
            );
        }

        return publicTodoDtoList;
    }




    /**
     * 공개 투두 아이템을 추가한다.
     * <br><br/>
     * 공개 투두 아이템이 추가되는 즉시, followSendCountRepository에 해당 공개 투두 아이템의
     * 주키와 동일한 주키를 가지는 엔티티를 저장해야 한다.
     * */
    @Transactional
    public boolean addPublicTodo(PublicTodoCreateRequest publicTodoCreateRequest) {
        if(!userRepository.findById(publicTodoCreateRequest.getAuthorUserPKId()).isPresent()){
            throw new SocialTodoException(ErrorCode.USER_NOT_FOUND);
        }

        validateContentLength(publicTodoCreateRequest.getPublicTodoContent());
        validateDateFormat(publicTodoCreateRequest.getDeadlineDate());
        validateDeadlineDate(publicTodoCreateRequest.getDeadlineDate());

        PublicTodoEntity savedEntity = publicTodoRepository.save(
            PublicTodoEntity.builder()
                .authorUserId(publicTodoCreateRequest.getAuthorUserPKId())
                .authorNickname(publicTodoCreateRequest.getAuthorUserNickname())
                .todoContent(publicTodoCreateRequest.getPublicTodoContent())
                .deadlineDate(CommonUtils.stringToDate(publicTodoCreateRequest.getDeadlineDate()))
                .isFinished(false)
                .build()
        );

        publicTodoSupportNagNumberRepository.save(
            SupportNagNumberEntity.builder()
                //방금 저장한 공개 투두 엔티티 주키와 동일한 주키 값을 가지도록 매핑해야 한다!!
                .id_DependsOnPublicTodoPK(savedEntity.getId())
                .numberOfSupport(0L)
                .numberOfNag(0L)
                .build()
        );

        return true;
    }






    /**
     * 공개 투두 아이템을 수정한다.
     * 한 번 공개한 공개 투투 아이템의 content는 수정이 불가능하다. 상황에 따라서 디데이 기한 또는 완료 여부를 수정할 수 있을 뿐이다.
     * 한 번 완료 처리한 공개 투두 아이템은 어떤 수정도 불가능하며 삭제만이 가능하다.
     * <br><br/>
     * 아직 완료 처리가 되지 않은 것을 전제했을 때, 수정이 가능한 경우는 오직 세 가지 뿐이고 각각의 경우에 대해서도 수정할 수 있는 범위가 정해져 있다.
     * <br><br/>
     * 오늘 마감 기한에 도달했다면, 완료 처리하는 것만 가능하다.
     * <br><br/>
     * 마감 기한이 도달하지 않았거나 이미 지나버렸다면, 마감기한과 완료 여부를 변경시킬 수 있다.
     * */
    @Transactional
    public boolean updatePublicTodo(PublicTodoUpdateRequest publicTodoUpdateRequest) {
        PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(publicTodoUpdateRequest.getPublicTodoPKId()).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
        );

        if(publicTodoEntity.isFinished()){
            throw new SocialTodoException(ErrorCode.CANNOT_UPDATE_FINISHED_PUBLIC_TODO_ITEM);
        }

        //찾아낸 원본 엔티티의 마감 기한이 오늘인 경우를 처리한다.
        if(
            CommonUtils.dateToString(LocalDateTime.now())
                .equals(CommonUtils.dateToString(publicTodoEntity.getDeadlineDate()))
        ){
            publicTodoEntity.setFinished(publicTodoUpdateRequest.isFinished());
            publicTodoRepository.save(publicTodoEntity);
            log.info("마감기한 도달한 날에 할일 완료처리");
        } else {
            //오늘이 아닌 경우를 처리한다. 즉, 마감기한이 아직 아니거나 이미 지나버린 경우다.
            validateDateFormat(publicTodoUpdateRequest.getDeadlineDate());
            validateDeadlineDate(publicTodoUpdateRequest.getDeadlineDate());

            publicTodoEntity.setDeadlineDate(CommonUtils.stringToDate(publicTodoUpdateRequest.getDeadlineDate()));
            publicTodoEntity.setFinished(publicTodoUpdateRequest.isFinished());
            publicTodoRepository.save(publicTodoEntity);
        }
        return true;
    }





    /**
     * 공개 투두 아이템을 삭제한다.
     * <br><br/>
     * 오늘 마감기한에 도달했는데, 아직 완료 처라가 되지 않은 경우 타임랴인 캐싱의 대상이 되므로 삭제가 불가능하다.
     * 그 이외에는 삭제가 가능하다.
     * */
    @Transactional
    public boolean removePublicTodo(Long publicTodoPKId) {

        PublicTodoEntity publicTodoEntity = publicTodoRepository.findById(publicTodoPKId).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PUBLIC_TODO_NOT_FOUND)
        );

        if(
            CommonUtils.dateToString(LocalDateTime.now())
                .equals(CommonUtils.dateToString(publicTodoEntity.getDeadlineDate()))
            &&
                !publicTodoEntity.isFinished()
        ){
            throw new SocialTodoException(ErrorCode.CANNOT_DELETE_TIMELINE_TARGET_TODO_ITEM);
        }

        publicTodoRepository.deleteById(publicTodoPKId);
        //publicTodoEntity에 대응되는 SupportNagNumberEntity도 항상 같이 삭제해 줘야 한다.
        publicTodoSupportNagNumberRepository.deleteById(publicTodoPKId);

        return true;
    }




    //------------ PRIVATE HELPER METHOD AREA ----------




    /**
     * 투두 컨텐츠의 길이는 0자 이상 100자 이내여야 한다.
     * */
    private void validateContentLength(String privateTodoContent){
        if(privateTodoContent.length() == 0 || privateTodoContent == null){
            throw new SocialTodoException(ErrorCode.ZERO_CONTENT_LENGTH);
        }
        if(privateTodoContent.length() > 100){
            throw new SocialTodoException(ErrorCode.CONTENT_LENGTH_TOO_LONG);
        }
    }



    /**
     * 오늘 만들어진 투두 아이템의 데드라인 날짜는 가장 빠르게 설정할 경우 오늘로 설정할 수 있고,
     * 가장 늦게 설정할 경우, 오늘로부터 365일이 지난 날짜까지 설정할 수 있다.
     * 그 이외의 디데이 설정은 전부 무효처리 한다.
     * */
    private void validateDeadlineDate(String dateInput){
        LocalDateTime deadlineDate = CommonUtils.stringToDate(dateInput);
        if(deadlineDate.isBefore(LocalDateTime.now() ) ){
            throw new SocialTodoException(ErrorCode.CANNOT_SET_PRIVATE_TODO_DEADLINE_ON_PAST);
        }
        if(deadlineDate.isAfter( LocalDateTime.now().plusDays(365) )){
            throw new SocialTodoException(ErrorCode.CANNOT_SET_PRIVATE_TODO_DEADLINE_AFTER_365DAYS);
        }
    }


    /**
     * "yyyy-mm-dd" 포맷의 날짜만 입력 받는다.
     * 프런트엔드에서 유저로부터 날짜를 입력 받는 from을 제공해주므로 2022-13-01
     * 같은 엉뚱한 날짜를 입력 받는 일은 없겠지만, SimpleDateFormat은
     * "2022-12-31-"과 같이 날짜가 끝난 다음에 오는 다른 문자가 있어도 이를 걸러내주지
     * 못하므로 별도로 이를 검사하는 코드를 먼저 실행하게 하였다.
     * */
    private void validateDateFormat(String deadlineDateString){
        if(!Character.isDigit(
            deadlineDateString.charAt(deadlineDateString.length()-1)
        )) throw new SocialTodoException(ErrorCode.INVALID_DEADLINE_DATE_FORMAT);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try{
            dateFormat.parse(deadlineDateString);
        }
        catch (Exception e){
            throw new SocialTodoException(ErrorCode.INVALID_DEADLINE_DATE_FORMAT);
        }
    }

}
















