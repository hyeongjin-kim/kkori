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

@Entity
@Table(name = "question_set_version")
public class QuestionSet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User ownerUserId;

    private String title;

    @OneToMany(mappedBy = "questionSetVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionSetTag> tags = new ArrayList<>();

    private String description;

    private Integer versionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_version_id")
    private QuestionSet parentVersionId;

    private Boolean isShared = false;

    @OneToMany(mappedBy = "questionSetVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionSetQuestionMap> questionMaps = new ArrayList<>();

}
