package com.carloscoral.consumer;


import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ObjectResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
}
