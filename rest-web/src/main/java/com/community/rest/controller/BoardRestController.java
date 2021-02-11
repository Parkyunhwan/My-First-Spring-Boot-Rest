package com.community.rest.controller;

import com.community.rest.domain.Board;
import com.community.rest.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/boards") // 복수형
public class BoardRestController {

    private BoardRepository boardRepository;

    public BoardRestController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // uri는 기본값, 반환값을 Json형태로 지정
    // Pageable객체 → page (페이지 번호), size(페이지 크기), sort 프로퍼티를 가짐
    public ResponseEntity<?> getBoards(@PageableDefault Pageable pageable) {
        Page<Board> boards = boardRepository.findAll(pageable);

        // 전체 페이지 수, 현재 페이지 번호, 총 게시판 수의 정보를 담는 PageMetadata 객체 생성 (PagedResources의 생성자의 인자로 사용될 예정)
        PageMetadata pageMetadata = new PageMetadata(pageable.getPageSize(), boards.getNumber(), boards.getTotalElements());

        // 리스트 컬렉션 형태의 boards와 메타데이터를 생성자에 넘겨 객체를 생성한다. 이 객체는 HATEOAS가 적용되며 페이징값까지 생성된 REST형 데이터를 만들어
        PagedResources<Board> resources = new PagedResources<>(boards.getContent(), pageMetadata);

        // 추가적으로 제공할 링크를 추가
        resources.add(linkTo(methodOn(BoardRestController.class).getBoards(pageable)).withSelfRel());
        return ResponseEntity.ok(resources);
    }


}
