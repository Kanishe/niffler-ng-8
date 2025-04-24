package guru.qa.niffler.data.entity.userAuth;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class AuthorityEntity implements Serializable {

    private UUID id;
    private Authority authority;
    private UUID userId;
}
