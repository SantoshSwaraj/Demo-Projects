package aisaac.payload.request;

import java.util.List;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateRequestBodyList<T> {


    @Valid
    private List<T> requestBody;
}
