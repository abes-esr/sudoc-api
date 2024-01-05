package fr.abes.sudoc.dto.provider;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonRootName("bacon")
public class ResultProviderDto {
    private BaconDto bacon;
}
