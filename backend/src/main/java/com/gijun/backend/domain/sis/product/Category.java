package com.gijun.backend.domain.sis.product;

import com.gijun.backend.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    @Column(nullable = false)
    private Integer level;

    @Column(name = "order_num", nullable = false)
    private Integer orderNum;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    @Builder
    public Category(String code, String name, Category parent, Integer orderNum, String description) {
        this.code = code;
        this.name = name;
        this.parent = parent;
        this.level = (parent == null) ? 1 : parent.getLevel() + 1;
        this.orderNum = orderNum;
        this.description = description;
    }
    public void updateInfo(String name, Category parent, Integer orderNum, String description) {
        if (name != null) this.name = name;
        if (parent != null && !parent.equals(this.parent)) {
            validateParent(parent);
            this.parent = parent;
            this.level = parent.getLevel() + 1;
        }
        if (orderNum != null) this.orderNum = orderNum;
        if (description != null) this.description = description;
    }

    private void validateParent(Category newParent) {
        if (this.equals(newParent)) {
            throw new IllegalArgumentException("자기 자신을 부모로 설정할 수 없습니다");
        }
        if (this.hasChildren() && newParent.isDescendantOf(this)) {
            throw new IllegalArgumentException("하위 카테고리를 상위 카테고리로 설정할 수 없습니다");
        }
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean isDescendantOf(Category ancestor) {
        if (this.parent == null) return false;
        if (this.parent.equals(ancestor)) return true;
        return this.parent.isDescendantOf(ancestor);
    }
}