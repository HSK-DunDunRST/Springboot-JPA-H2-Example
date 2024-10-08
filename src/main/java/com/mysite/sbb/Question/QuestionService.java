package com.mysite.sbb.Question;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.mysite.sbb.Answer.Answer;
import com.mysite.sbb.User.SiteUser;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.mysite.sbb.DataNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    /* 질문 리스트 불러오기 */
    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.questionRepository.findAllByKeyword(kw, pageable);
    }

    /* id로 질문 불러오기 */
    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    /* 질문 등록 서비스 */
    public void create(String subject, String content, SiteUser author) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(author);
        this.questionRepository.save(question);
    }

    /* 질문 내용 수정 서비스 */
    public void modify(Question question, String subject, String content){
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    /* 질문 내용 삭제 서비스 */
    public void delete(Question question){
        this.questionRepository.delete(question);
    }

    /* 질문 추천 서비스 */
    public void vote(Question question, SiteUser siteUser){
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String keywords){
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                query.distinct(true);
                Join<Question, SiteUser> user1 = root.join("author", JoinType.LEFT);
                Join<Question, Answer> answer = root.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> user2 = root.join("author", JoinType.LEFT);
                return criteriaBuilder.or(criteriaBuilder.like(root.get("subject"), "%" + keywords + "%"),
                    criteriaBuilder.like(root.get("content"), "%" + keywords + "%"),
                    criteriaBuilder.like(root.get("username"), "%" + keywords + "%"),
                    criteriaBuilder.like(root.get("content"), "%" + keywords + "%"),
                    criteriaBuilder.like(root.get("username"), "%" + keywords + "%"));
            }
        };
    }
}
