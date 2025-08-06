package com.kkori.service;

import static com.kkori.constant.QuestionSetConstants.DefaultValue.DEFAULT_SHARED_STATUS;
import static com.kkori.constant.QuestionSetConstants.DefaultValue.INITIAL_VERSION_NUMBER;
import static com.kkori.constant.QuestionSetConstants.DefaultValue.NEXT_VERSION_NUMBER;
import static com.kkori.entity.QuestionSetQuestionMap.of;
import static com.kkori.entity.QuestionType.fromCode;
import static java.util.Comparator.comparingInt;

import com.kkori.dto.question.request.CreateNewQuestionSetRequest;
import com.kkori.dto.question.request.CreateQuestionRequest;
import com.kkori.entity.Question;
import com.kkori.entity.QuestionSet;
import com.kkori.entity.QuestionSetQuestionMap;
import com.kkori.entity.User;
import com.kkori.exception.questionset.QuestionSetException;
import com.kkori.exception.user.UserException;
import com.kkori.repository.QuestionRepository;
import com.kkori.repository.QuestionSetRepository;
import com.kkori.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionSetServiceImpl implements QuestionSetService {

    private final QuestionSetRepository questionSetRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Override
    public Long createQuestionSetWithInitialQuestions(Long userId, CreateNewQuestionSetRequest request, String title) {
        validateCreateQuestionSetRequest(request, title);

        if (request.getParentVersionId() != null) {
            return createVersionedQuestionSetFromParent(userId, request, title);
        }

        return createBrandNewQuestionSetWithQuestions(userId, request, title);
    }

    private void validateCreateQuestionSetRequest(CreateNewQuestionSetRequest request, String title) {
        if (title == null || title.trim().isEmpty()) {
            throw QuestionSetException.noTitle();
        }
        validateQuestionsNotEmpty(request.getQuestions());
    }

    private void validateQuestionsNotEmpty(List<CreateQuestionRequest> questions) {
        Optional.ofNullable(questions)
                .filter(list -> !list.isEmpty())
                .orElseThrow(QuestionSetException::emptyQuestions);
    }

    @Override
    public Long createNewQuestionSet(Long userId, String title, String description) {
        User user = findUserById(userId);

        QuestionSet questionSet = createQuestionSetEntity(user, title, description, null, INITIAL_VERSION_NUMBER);
        QuestionSet savedQuestionSet = questionSetRepository.save(questionSet);
        
        return savedQuestionSet.getId();
    }

    private QuestionSet createQuestionSetEntity(User user, String title, String description, QuestionSet parentVersion, Integer versionNumber) {
        return QuestionSet.builder()
                .ownerUserId(user)
                .title(title)
                .description(description)
                .versionNumber(versionNumber)
                .parentVersionId(parentVersion)
                .isShared(DEFAULT_SHARED_STATUS)
                .build();
    }

    @Override
    public Long addQuestionToQuestionSet(Long userId, Long questionSetId, CreateQuestionRequest request) {

        findUserById(userId);

        QuestionSet questionSet = findQuestionSetById(questionSetId);

        validateQuestionSetOwnership(questionSet, userId);

        Question savedQuestion = createAndSaveQuestion(request);
        addQuestionToQuestionSetMapping(questionSet, savedQuestion);

        return savedQuestion.getId();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);
    }

    private QuestionSet findQuestionSetById(Long questionSetId) {
        return questionSetRepository.findById(questionSetId)
                .orElseThrow(QuestionSetException::questionSetNotFound);
    }

    private void validateQuestionSetOwnership(QuestionSet questionSet, Long userId) {
        if (!questionSet.getOwnerUserId().getUserId().equals(userId)) {
            throw QuestionSetException.noPermission();
        }
    }

    private Question createAndSaveQuestion(CreateQuestionRequest request) {
        Question question = Question.builder()
                .content(request.getContent())
                .questionType(fromCode(request.getQuestionType()))
                .expectedAnswer(request.getExpectedAnswer())
                .build();

        return questionRepository.save(question);
    }

    private Long createBrandNewQuestionSetWithQuestions(Long userId, CreateNewQuestionSetRequest request, String title) {
        Long questionSetId = createNewQuestionSet(userId, title, request.getDescription());
        addMultipleQuestionsToSet(userId, questionSetId, request.getQuestions());
        return questionSetId;
    }

    private Long createVersionedQuestionSetFromParent(Long userId, CreateNewQuestionSetRequest request, String title) {
        User user = findUserById(userId);
        QuestionSet parentQuestionSet = findQuestionSetById(request.getParentVersionId());
        
        validateQuestionSetOwnership(parentQuestionSet, userId);
        
        Integer nextVersionNumber = getNextVersionNumber(request.getParentVersionId());
        QuestionSet newVersionQuestionSet = createQuestionSetEntity(user, title, request.getDescription(), parentQuestionSet, nextVersionNumber);
        QuestionSet savedQuestionSet = questionSetRepository.save(newVersionQuestionSet);
        
        linkExistingQuestionsToNewVersion(parentQuestionSet, savedQuestionSet);
        addMultipleQuestionsToSet(userId, savedQuestionSet.getId(), request.getQuestions());
        
        return savedQuestionSet.getId();
    }
    
    private Integer getNextVersionNumber(Long parentVersionId) {
        return questionSetRepository.findMaxVersionNumberByParentVersionId(parentVersionId)
                .map(maxVersion -> maxVersion + 1)
                .orElse(NEXT_VERSION_NUMBER);
    }
    
    private void linkExistingQuestionsToNewVersion(QuestionSet parentQuestionSet, QuestionSet childQuestionSet) {
        parentQuestionSet.getQuestionMaps().stream()
                .sorted(comparingInt(QuestionSetQuestionMap::getDisplayOrder))
                .forEach(parentQuestionMap -> {
                    Question existingQuestion = parentQuestionMap.getQuestion();
                    addQuestionToQuestionSetMapping(childQuestionSet, existingQuestion, parentQuestionMap.getDisplayOrder());
                });
    }
    

    private void addMultipleQuestionsToSet(Long userId, Long questionSetId, List<CreateQuestionRequest> questions) {
        questions.forEach(singleQuestionRequest -> addQuestionToQuestionSet(userId, questionSetId, singleQuestionRequest));
    }

    private void addQuestionToQuestionSetMapping(QuestionSet questionSet, Question savedQuestion) {
        int nextDisplayOrder = questionSet.getQuestionMaps().size() + 1;
        addQuestionToQuestionSetMapping(questionSet, savedQuestion, nextDisplayOrder);
    }
    
    private void addQuestionToQuestionSetMapping(QuestionSet questionSet, Question savedQuestion, int displayOrder) {
        QuestionSetQuestionMap questionMap = of(
                questionSet, 
                savedQuestion, 
                displayOrder
        );
        
        questionSet.getQuestionMaps().add(questionMap);
    }

}
