package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.login.domain.dto.DataDTO;
import com.wwme.wwme.login.domain.dto.ErrorDTO;
import com.wwme.wwme.login.domain.dto.SuccessDTO;
import com.wwme.wwme.login.service.WithdrawService;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/withdraw")
public class WithdrawController {
    private final WithdrawService withdrawService;

    @PostMapping
    public ResponseEntity<?> withdrawUser(@Login User user) {
        try {
            withdrawService.withdrawUser(user);
            SuccessDTO successDTO = new SuccessDTO(true);

            return new ResponseEntity<>(new DataDTO(successDTO), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDTO errorDTO = new ErrorDTO(e.getMessage());
            return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
