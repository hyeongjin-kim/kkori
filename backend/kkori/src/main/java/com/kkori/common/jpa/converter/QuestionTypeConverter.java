package com.kkori.common.jpa.converter;

import com.kkori.entity.QuestionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class QuestionTypeConverter implements AttributeConverter<QuestionType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(QuestionType questionType) {
        return questionType == null ? null : questionType.getCode();
    }

    @Override
    public QuestionType convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : QuestionType.fromCode(dbData);
    }

}
