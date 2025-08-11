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

    private Boolean isPublic = false;
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionSetQuestionMap> questionMaps = new ArrayList<>();

    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionSetTag> questionSetTags = new ArrayList<>();

    @Version
    @Column(name = "version")
    private Long version;

    @Builder
    public QuestionSet(User ownerUserId, String title, String description,
                       Integer versionNumber, QuestionSet parentVersionId, Boolean isPublic) {
        this.ownerUserId = ownerUserId;
        this.title = title;
        this.description = description;
        this.versionNumber = versionNumber;
        this.parentVersionId = parentVersionId;
        this.isPublic = isPublic != null ? isPublic : false;
    }

    public static QuestionSet createNew(User owner, String title, String description) {
        return QuestionSet.builder()
                .ownerUserId(owner)
                .title(title)
                .description(description)
                .versionNumber(1)
                .parentVersionId(null)
                .isPublic(false)
                .build();
    }

    public static QuestionSet createVersion(QuestionSet parent, User owner, String title, String description) {
        return QuestionSet.builder()
                .ownerUserId(owner)
                .title(title != null ? title : parent.getTitle())
                .description(description != null ? description : parent.getDescription())
                .versionNumber(parent.getVersionNumber() + 1)
                .parentVersionId(parent)
                .isPublic(false)
                .build();
    }

    public static QuestionSet copy(QuestionSet original, User newOwner, String newTitle, String newDescription) {
        return QuestionSet.builder()
                .ownerUserId(newOwner)
                .title(newTitle != null ? newTitle : original.getTitle())
                .description(newDescription != null ? newDescription : original.getDescription())
                .versionNumber(1)
                .parentVersionId(original)
                .isPublic(false)
                .build();
    }

    public void updatePublicStatus(Boolean isPublic) {
        this.isPublic = isPublic != null ? isPublic : false;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public boolean canBeAccessedBy(Long userId) {
        return this.ownerUserId.getUserId().equals(userId) || this.isPublic;
    }

    public boolean isOwner(Long userId) {
        return this.ownerUserId.getUserId().equals(userId);
    }
}
