package com.maen.vlogwebserviceserver.web;


import com.maen.vlogwebserviceserver.service.comments.CommentsService;
import com.maen.vlogwebserviceserver.service.posts.MediaService;
import com.maen.vlogwebserviceserver.service.posts.PostsService;
import com.maen.vlogwebserviceserver.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;
    private final MediaService mediaService;
    private final CommentsService commentsService;

    // posts 저장 multipart/form-data
    @PostMapping("api/v1/posts")
    public Long save(@ModelAttribute PostsSaveRequestDto postsSaveRequestDto) throws IOException {
       return postsService.save(postsSaveRequestDto);
    }

    // 메인화면 posts 리스트 처음 불러오기
    @GetMapping("api/v1/posts")
    public List<PostsAllResponseDto> findAllInMainPage() {
        return postsService.findAllByParams(null,null);
    }
    // 메인화면 posts 리스트 스크롤로 불러오기
    @GetMapping("api/v1/posts/{last_post_id}")
    public List<PostsAllResponseDto> findAllInMainPage(@PathVariable Long last_post_id) {
        return postsService.findAllByParams(null,last_post_id);
    }
    //태그 검색 후 첫 리스트 조회
    @GetMapping("api/v1/posts/{tag}/search")
    public List<PostsAllResponseDto> findAllByTag(@PathVariable String tag) {
        return postsService.findAllByParams(tag, null);
    }
    //태그 검색 후 스크롤 한 다음 리스트 조회
    @GetMapping("api/v1/posts/{tag}/search/{last_posts_id}")
    public List<PostsAllResponseDto> findAllByTag(@PathVariable String tag, @PathVariable Long last_posts_id) {
        return postsService.findAllByParams(tag, last_posts_id);
    }



    // posts 디테일 메타데이터
    @GetMapping("api/v1/posts/{id}/detail")
    public PostsDetailResponseDto findById(@PathVariable Long id) {
        return postsService.findById(id);
    }
    //posts 디테일 댓글 처음 불러오기
    @GetMapping("api/v1/posts/{postsId}/comments")
    public List<CommentsAllResponseDto> findAllInPostsDetail(@PathVariable Long postsId) {
        return commentsService.findAllInPostsDetail(postsId,null);
    }
    //posts 디테일 댓글 스크롤로 불러오기
    @GetMapping("api/v1/posts/{postsId}/comments/{commentsId}")
    public List<CommentsAllResponseDto> findAllInPostsDetail(@PathVariable Long postsId, @PathVariable Long commentsId) {
        return commentsService.findAllInPostsDetail(postsId,commentsId);
    }
    //posts 디테일 영상 스트리밍 재생
    @GetMapping("api/v1/posts/video/{videoName}")
    public ResponseEntity<ResourceRegion> findVideoByName(@RequestHeader HttpHeaders httpHeaders, @PathVariable String videoName) throws Exception{
        return mediaService.findVideoByName(httpHeaders,videoName);
    }
    // 메인화면 posts 리스트 속 썸네일 조회
    @GetMapping(value = "api/v1/posts/thumbnail/{thumbnailName}")
    public ResponseEntity<byte[]> findThumbnailByName(@PathVariable String thumbnailName) throws IOException {
        return mediaService.findThumbnailByName(thumbnailName);
    }






//    @PutMapping("api/v1/posts/{id}")
//    public Long update() {
//
//    }
//
//
//    @DeleteMapping("api/v1/posts/{id}")
//    public Long delete() {
//
//    }




}
