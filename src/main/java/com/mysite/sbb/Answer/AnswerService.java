package com.mysite.sbb.Answer;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.Question.Question;
import com.mysite.sbb.User.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    /* 답변글 생성하는 서비스 */
    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setAuthor(author);
        answer.setQuestion(question);
        this.answerRepository.save(answer);
        return answer;
    }
    /* ID로 답변 조회하기 */
    public Answer getAnswer(Integer id){
        Optional<Answer> answer = this.answerRepository.findById(id);
        
        if (answer.isPresent()){
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }        
    }
    /* 답변 수정 서비스 */
    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }
    /* 답변 삭제 서비스 */
    public void delete(Answer answer){
        this.answerRepository.delete(answer);
    }
    /* 답변 추천 서비스 */
    public void vote(Answer answer, SiteUser siteUser){
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }
}
