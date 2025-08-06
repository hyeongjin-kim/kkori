package com.kkori.entity;

import com.kkori.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "question_set")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionSet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User ownerUserId;

    private String title;

    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionSetTag> tags = new ArrayList<>();

    private String description;

    private Integer versionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_version_id")
    private QuestionSet parentVersionId;

    private Boolean isShared = false;
    private Boolean isDelete = false;

    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionSetQuestionMap> questionMaps = new ArrayList<>();

    @Builder
    public QuestionSet(User ownerUserId, String title, String description,
                       Integer versionNumber, QuestionSet parentVersionId, Boolean isShared) {
        this.ownerUserId = ownerUserId;
        this.title = title;
        this.description = description;
        this.versionNumber = versionNumber;
        this.parentVersionId = parentVersionId;
        this.isShared = isShared != null ? isShared : false;
    }

}
