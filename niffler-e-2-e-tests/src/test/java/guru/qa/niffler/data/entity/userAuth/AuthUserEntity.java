package guru.qa.niffler.data.entity.userAuth;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthUserEntity implements Serializable {

    public AuthUserEntity(UUID id) {
        this.id = id;
    }

    public AuthUserEntity() {
    }

    private UUID id;

    private String username;

    private String password;

    private Boolean enabled;

    private Boolean accountNonExpired;

    private Boolean accountNonLocked;

    private Boolean credentialsNonExpired;
}
