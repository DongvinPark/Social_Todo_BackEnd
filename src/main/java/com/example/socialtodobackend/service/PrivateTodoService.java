package com.example.socialtodobackend.service;

import com.example.socialtodobackend.dto.PrivateTodoDto;
import com.example.socialtodobackend.entity.PrivateTodoEntity;
import com.example.socialtodobackend.exception.SocialTodoException;
import com.example.socialtodobackend.repository.PrivateTodoRepository;
import com.example.socialtodobackend.type.ErrorCode;
import com.example.socialtodobackend.utils.CommonUtils;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrivateTodoService {

    private final PrivateTodoRepository privateTodoRepository;


    /**
     * 프라이빗 투두 아이템 한 개를 추가하고, 그 후의 프라이빗 투두 리스트를 반환한다.
     * */
    @Transactional
    public PrivateTodoEntity createPrivateTodoEntity(
        Long authorUserPKId,
        String todoContent,
        String deadLineDateString
    ) {
        validateContentLength(todoContent);
        validateDateFormat(deadLineDateString);
        validateDeadlineDate(deadLineDateString);

        return privateTodoRepository.save(
                PrivateTodoEntity.builder()
                    .authorUserId(authorUserPKId)
                    .todoContent(todoContent)
                    .isFinished(false)
                    .deadlineDate(CommonUtils.stringToDate(deadLineDateString))
                    .build()
        );
    }



    /**
     * 특정한 사용자가 작성한 모든 프라이빗 투두 아이템 리스트를 반환한다.
     * 인증 기능을 도입할 때, 내가 쓴 것만 확인할 수 있도록 조치해야 한다.
     * */
    @Transactional
    public List<PrivateTodoDto> getAllPrivateTodo(long authorUserPKId) {
        List<PrivateTodoEntity> privateTodoEntityList = privateTodoRepository.findAllByAuthorUserId(authorUserPKId);

        List<PrivateTodoDto> privateTodoDtoList = new ArrayList<>();
        for(PrivateTodoEntity privateTodoEntity : privateTodoEntityList){
            privateTodoDtoList.add(
                PrivateTodoDto.fromEntity(privateTodoEntity)
            );
        }

        return privateTodoDtoList;
    }



    /**
     * 프라이빗 투두 아이템 1개를 수정한 후, 수정된 결과 리스트를 반환한다.
     * 프라이빗 투두 아이템에서 유저가 수정할 수 있는 것은 해당 투두 아이템의 내용뿐이다.
     * 또한 자기 자신의 프라이빗 투두만을 수정할 수 있다.
     * */
    @Transactional
    public PrivateTodoEntity updatePrivateTodoEntity(PrivateTodoDto privateTodoDto) {

        validateContentLength(privateTodoDto.getTodoContent());
        validateDateFormat(privateTodoDto.getDeadlineDate());
        validateDeadlineDate(privateTodoDto.getDeadlineDate());

        PrivateTodoEntity privateTodoEntity = privateTodoRepository.findById(privateTodoDto.getId()).orElseThrow( () -> new SocialTodoException(
            ErrorCode.PRIVATE_TODO_NOT_FOUND) );

        privateTodoEntity.setTodoContent(privateTodoDto.getTodoContent());
        privateTodoEntity.setFinished(privateTodoDto.isFinished());
        privateTodoEntity.setDeadlineDate(CommonUtils.stringToDate(privateTodoDto.getDeadlineDate()));

        privateTodoRepository.save(privateTodoEntity);

        return privateTodoEntity;
    }


    /**
     * 프라이빗 투두를 삭제하거나, 완료했을 때
     * 해당 투두 아이템을 삭제완료 하고 그 후의 프라이빗 투두 리스트를 반환한다.
     * JPA는 delete 할 때 비효율적인 측면이 있으므로
     * 추후에 JPA의 delete메서드를 거치지 않고 DB에 쿼리를 직접 날리는 방식으로 수정하는 것이 필요하다.
     * */
    @Transactional
    public boolean deletePrivateTodo(PrivateTodoDto privateTodoDto) {
        PrivateTodoEntity privateTodoEntity = privateTodoRepository.findById(privateTodoDto.getId()).orElseThrow(
            () -> new SocialTodoException(ErrorCode.PRIVATE_TODO_NOT_FOUND)
        );
        privateTodoRepository.deleteById(privateTodoEntity.getId());

        return true;
    }



    //------------------ PRIVATE HELPER METHODS ------------

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




















