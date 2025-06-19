package com.wedvice.subtask.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/subtask")
public class SubTaskController {

    @GetMapping
    public String get(){
        return "get paging subtask";
    }

    @PatchMapping("/align")
    public String patchAlign(){
        return "subtask 정렬 위치 변경";
    }


    @PostMapping()
    public String post(){
        return "create subtask";
    }

    @DeleteMapping()
    public String delete(){
        return "delete subtask";
    }


}
