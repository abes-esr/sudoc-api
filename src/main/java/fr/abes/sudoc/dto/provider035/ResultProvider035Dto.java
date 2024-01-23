package fr.abes.sudoc.dto.provider035;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonRootName("bacon")
public class ResultProvider035Dto {
    private BaconDto bacon;
}
