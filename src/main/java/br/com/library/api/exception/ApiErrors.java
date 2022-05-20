package br.com.library.api.exception;

import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErrors {

    private List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErrors(BusinessException args) {
        this.errors = Arrays.asList(args.getMessage());
    }

    public ApiErrors(ResponseStatusException args) {
        this.errors = Arrays.asList(args.getReason());
    }

    public List<String> getErrors() {
        return this.errors;
    }


}
