package com.kkori.entity;

import com.kkori.common.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "question_sets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionSet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_set_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User ownerUserId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_version_id")
    private QuestionSet parentVersionId;

    @Column(name = "is_shared", nullable = false)
    private Boolean isShared = false;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionSetTag> questionSetTags = new ArrayList<>();

    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionSetQuestionMap> questionMaps = new ArrayList<>();

    @Version
    @Column(name = "version")
    private Long version;

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

    public static QuestionSet createNew(User owner, String title, String description) {
        return QuestionSet.builder()
                .ownerUserId(owner)
                .title(title)
                .description(description)
                .versionNumber(1)
                .parentVersionId(null)
                .isShared(false)
                .build();
    }

    public static QuestionSet createVersion(QuestionSet parent, User owner, String title, String description) {
        return QuestionSet.builder()
                .ownerUserId(owner)
                .title(title != null ? title : parent.getTitle())
                .description(description != null ? description : parent.getDescription())
                .versionNumber(parent.getVersionNumber() + 1)
                .parentVersionId(parent)
                .isShared(false)
                .build();
    }

    public static QuestionSet copy(QuestionSet original, User newOwner, String newTitle, String newDescription) {
        return QuestionSet.builder()
                .ownerUserId(newOwner)
                .title(newTitle != null ? newTitle : original.getTitle())
                .description(newDescription != null ? newDescription : original.getDescription())
                .versionNumber(1)
                .parentVersionId(original)
                .isShared(false)
                .build();
    }

    public void updateSharedStatus(Boolean isShared) {
        this.isShared = isShared != null ? isShared : false;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public boolean canBeAccessedBy(Long userId) {
        return this.ownerUserId.getUserId().equals(userId) || this.isShared;
    }

    public boolean isOwner(Long userId) {
        return this.ownerUserId.getUserId().equals(userId);
    }
}
