package com.kkori.repository;

import com.kkori.entity.QuestionSetItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionSetItemRepository extends JpaRepository<QuestionSetItem, Long> {

    List<QuestionSetItem> findByQuestionSetSetIdOrderBySortOrderAsc(Long questionSetId);

}
