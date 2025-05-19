package guru.qa.niffler.data.entity.category;

import guru.qa.niffler.model.CategoryJson;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CategoryEntity implements Serializable {

    private UUID id;
    private String name;
    private String username;
    private Boolean archived;

    public CategoryEntity(UUID id) {
        this.id = id;
    }

    public CategoryEntity() {
    }

    public static CategoryEntity fromJson(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(categoryJson.id());
        categoryEntity.setName(categoryJson.name());
        categoryEntity.setUsername(categoryJson.username());
        categoryEntity.setArchived(categoryJson.archived());

        return categoryEntity;
    }
}
