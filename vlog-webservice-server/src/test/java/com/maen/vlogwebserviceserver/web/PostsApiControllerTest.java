package com.maen.vlogwebserviceserver.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.maen.vlogwebserviceserver.domain.comments.Comments;
import com.maen.vlogwebserviceserver.domain.comments.CommentsRepository;
import com.maen.vlogwebserviceserver.domain.posts.*;
import com.maen.vlogwebserviceserver.domain.user.User;
import com.maen.vlogwebserviceserver.domain.user.UserRepository;
import com.maen.vlogwebserviceserver.service.posts.PostsService;
import com.maen.vlogwebserviceserver.web.dto.CommentsAllResponseDto;
import com.maen.vlogwebserviceserver.web.dto.PostsAllResponseDto;
import com.maen.vlogwebserviceserver.web.dto.PostsSaveRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PostsApiControllerTest {

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private PostsTagsRepository postsTagsRepository;

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private PostsService postsService;

    @Autowired
    private MockMvc mvc;

    @LocalServerPort
    private int port;

    @AfterEach
    public void tearDown() throws Exception{
        postsTagsRepository.deleteAll();
        commentsRepository.deleteAll();
        postsRepository.deleteAll();
        tagsRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void Posts_????????????() throws Exception {
        //given
        String description = "??????";
        String tags = "#??????#??????#??????";
        Long userId = 1L;
        MockMultipartFile multipartFile = new MockMultipartFile("video","kk.mp4", "video/mp4",new FileInputStream("C:\\Users\\Bang\\Desktop\\kk.mp4"));
        String url = "http://localhost:"+port+"/api/v1/posts";
        tagsRepository.save(Tags.builder()
                .content("??????")
                .build());

        //when
        mvc.perform(multipart(url)
                .file(multipartFile)
                .param("userId", String.valueOf(userId))
                .param("tags", tags)
                .param("description",description))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        List<Posts> postsList = postsRepository.findAll();
        List<PostsTags> postsTagsList = postsTagsRepository.findAll();
        List<Tags> tagsList = tagsRepository.findAll();
        Posts posts = postsList.get(0);

        for(int i = 0; i < postsTagsList.size(); i++){
            PostsTags postsTags = postsTagsList.get(i);
            Tags tag = tagsList.get(i);
            assertThat(postsTags.getPostsId()).isEqualTo(posts.getId());
            assertThat(postsTags.getTagsId()).isEqualTo(tag.getId());
            System.out.print(tag.getHashTagContent()+" : ");
            System.out.println(tag.getCount());
        }

        assertThat(posts.getUserId()).isEqualTo(userId);
        assertThat(posts.getDescription()).isEqualTo(description);
        System.out.println("########## "+posts.getVideoName());
        System.out.println("########## "+posts.getThumbnailName());

    }

    @Test
    public void Posts_detail_????????????() throws Exception {
        //given
        String description = "??????";
        String tags = "#??????#??????#??????";
        Long userId = 1L;
        MockMultipartFile multipartFile = new MockMultipartFile("video","test.mp4", "video/mp4",new FileInputStream("C:\\Users\\Bang\\Desktop\\test.mp4"));

        Long postsId = postsService.save(PostsSaveRequestDto.builder()
                .tags(tags)
                .userId(userId)
                .description(description)
                .video(multipartFile)
                .build());

        String name = "?????????";
        String email = "abc@123.com";
        String picture = "??????";

        userRepository.save(User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .build());

        String url = "http://localhost:"+port+"/api/v1/posts/"+postsId+"/detail";

        //then
        mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void main??????_???posts??????_????????????() throws Exception {
        //given
        String description = "??????";
        String tags = "#??????#??????#??????";
        String userName = "?????????";
        String userPic = "?????????";
        String email = "?????????";
        List<String> expectedTag = new ArrayList<>();
        expectedTag.add("#??????");
        expectedTag.add("#??????");
        expectedTag.add("#??????");


        MockMultipartFile multipartFile = new MockMultipartFile("video","test.mp4", "video/mp4",new FileInputStream("C:\\Users\\Bang\\Desktop\\test.mp4"));
        List<PostsAllResponseDto> responseDtoList = new ArrayList<>();
        int lastSavedPostsId = 16;
        int postsListSize = 12;

        for(int i = 1 ; i<lastSavedPostsId; i++) {
            Long userId = userRepository.save(User.builder()
                    .name(userName)
                    .email(email)
                    .picture(userPic)
                    .build()).getId();
            Long postsId = postsService.save(PostsSaveRequestDto.builder()
                    .userId(userId)
                    .description(description)
                    .tags(tags)
                    .video(multipartFile)
                    .build());
            if(i>=lastSavedPostsId-postsListSize) {
                Posts posts = postsRepository.findById(postsId).orElseThrow(()->new IllegalArgumentException("?????? posts"));
                responseDtoList.add(PostsAllResponseDto.builder()
                        .authorId(userId)
                        .postsId(postsId)
                        .authorName(userName)
                        .views(posts.getViews())
                        .thumbnailName(posts.getThumbnailName())
                        .tags(expectedTag)
                        .postsLike(0)
                        .build());
            }
        }

        String url = "http://localhost:"+port+"/api/v1/posts/recent";

        //when
        mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                //then
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDtoList)));
    }

    @Test
    public void main??????_???????????????posts??????_????????????() throws Exception{
        //given
        String description = "??????";
        String tags = "#??????#??????#??????";
        String userName = "?????????";
        String userPic = "?????????";
        String email = "?????????";
        List<String> expectedTag = new ArrayList<>();
        expectedTag.add("#??????");
        expectedTag.add("#??????");
        expectedTag.add("#??????");

        MockMultipartFile multipartFile = new MockMultipartFile("video","test.mp4", "video/mp4",new FileInputStream("C:\\Users\\Bang\\Desktop\\test.mp4"));
        List<PostsAllResponseDto> responseDtoList = new ArrayList<>();
        int lastSavedPostsId = 13;
        int lastReadPostsId = 8;

        for(int i = 1 ; i<lastSavedPostsId; i++) {
            Long userId = userRepository.save(User.builder()
                    .name(userName)
                    .email(email)
                    .picture(userPic)
                    .build()).getId();
            Long postsId = postsService.save(PostsSaveRequestDto.builder()
                    .userId(userId)
                    .description(description)
                    .tags(tags)
                    .video(multipartFile)
                    .build());
            if(i<lastReadPostsId) {
                Posts posts = postsRepository.findById(postsId).orElseThrow(()->new IllegalArgumentException("?????? posts"));
                responseDtoList.add(PostsAllResponseDto.builder()
                        .authorId(userId)
                        .postsId(postsId)
                        .authorName(userName)
                        .views(posts.getViews())
                        .thumbnailName(posts.getThumbnailName())
                        .tags(expectedTag)
                        .postsLike(0)
                        .build());
            }
        }

        String url = "http://localhost:"+port+"/api/v1/posts/"+lastReadPostsId+"/recent";

        //when
        mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                //then
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDtoList)));
    }



    @Test
    public void posts_detail??????_???comments??????_????????????() throws Exception {
        // commentsId == null ?????? id ???????????? ?????? 12???

        //given
        List<CommentsAllResponseDto> responseDtoList = new ArrayList<>();
        Long postsId = 1L;
        int lateSavedCommentsId = 20;
        int commentsListSize = 12;
        String userName = "?????????";
        String userPic = "??????";
        String email = "?????????";

        for(int i = 1; i < lateSavedCommentsId; i++) {
            Long userId = userRepository.save(User.builder()
                    .name(userName)
                    .picture(userPic)
                    .email(email)
                    .build()).getId();
            commentsRepository.save(Comments.builder()
                    .userId(userId)
                    .postsId(postsId)
                    .content(String.valueOf(i))
                    .build());
            if(i>=lateSavedCommentsId-commentsListSize){
                responseDtoList.add(CommentsAllResponseDto.builder()
                        .author(userName)
                        .contents(String.valueOf(i))
                        .commentsLike(0)
                        .build());
            }
        }

        String url = "http://localhost:"+port+"/api/v1/posts/"+postsId+"/comments";

        //when
        mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                //then
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDtoList)));
    }

    @Test
    public void posts_detail??????_???????????????comments??????_????????????() throws Exception {
        // commentsId == n ?????? n+1 ?????? ?????? 12???

        //given
        List<CommentsAllResponseDto> responseDtoList = new ArrayList<>();
        Long postsId = 1L;
        int lateSavedCommentsId = 20;
        int lastReadCommentsId = 8;
        String userName = "?????????";
        String userPic = "??????";
        String email = "?????????";

        for(int i = 1; i < lateSavedCommentsId; i++) {
            Long userId = userRepository.save(User.builder()
                    .name(userName)
                    .picture(userPic)
                    .email(email)
                    .build()).getId();
            commentsRepository.save(Comments.builder()
                    .userId(userId)
                    .postsId(postsId)
                    .content(String.valueOf(i))
                    .build());
            if(i<lastReadCommentsId){
                responseDtoList.add(CommentsAllResponseDto.builder()
                        .author(userName)
                        .contents(String.valueOf(i))
                        .commentsLike(0)
                        .build());
            }
        }

        String url = "http://localhost:"+port+"/api/v1/posts/"+postsId+"/comments/"+lastReadCommentsId;

        //when
        mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                //then
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDtoList)));


    }

    @Test
    public void ???????????????_???_posts??????_????????????() throws Exception {
        //given
        String tag = "??????";
        String[] tagList = {"??????","??????","????????????","??????"};
        List<PostsAllResponseDto> responseDtoList = new ArrayList<>();

        int idx = 0;
        for(int i = 0; i <= 12; i++) {
            if(idx>=tagList.length) {
                idx = 0;
            }
            if(i<tagList.length) {
                tagsRepository.save(Tags.builder()
                        .content(tagList[i])
                        .build());
            }
            String tmp = String.valueOf(i+1);
            Long userId = userRepository.save(User.builder()
                    .name(tmp)
                    .email(tmp)
                    .picture(tmp)
                    .build()).getId();
            Long postsId = postsRepository.save(Posts.builder()
                    .userId(userId)
                    .description(tmp)
                    .thumbnailName(tmp)
                    .videoName(tmp)
                    .build()).getId();
            postsTagsRepository.save(PostsTags.builder()
                    .postsId(postsId)
                    .tagsId((long) idx+1)
                    .build());
            // ?????? ?????? Dto = "??????"????????? ????????? ?????????
            if(tagList[idx].contains("??????")) {
                responseDtoList.add(PostsAllResponseDto.builder()
                        .authorName(tmp)
                        .postsLike(0)
                        .thumbnailName(tmp)
                        .views(0)
                        .tags(Collections.singletonList("#"+tagList[idx]))
                        .authorId(userId)
                        .postsId(postsId)
                        .build());
            }
            idx++;
        }

        String url = "http://localhost:"+port+"/api/v1/posts/"+tag+"/search/recent";

        //when
        mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                //then
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDtoList)));
    }

    @Test
    public void ???????????????_????????????_??????_posts??????_????????????() throws Exception {
        //given
        String tag = "??????";
        String[] tagList = {"??????","??????","????????????","??????"};
        List<PostsAllResponseDto> responseDtoList = new ArrayList<>();
        long last_posts_id = 6;
        int idx = 0;
        for(int i = 0; i <= 12; i++) {
            if(idx>=tagList.length) {
                idx = 0;
            }
            if(i<tagList.length) {
                tagsRepository.save(Tags.builder()
                        .content(tagList[i])
                        .build());
            }
            String tmp = String.valueOf(i+1);
            Long userId = userRepository.save(User.builder()
                    .name(tmp)
                    .email(tmp)
                    .picture(tmp)
                    .build()).getId();
            Long postsId = postsRepository.save(Posts.builder()
                    .userId(userId)
                    .description(tmp)
                    .thumbnailName(tmp)
                    .videoName(tmp)
                    .build()).getId();
            postsTagsRepository.save(PostsTags.builder()
                    .postsId(postsId)
                    .tagsId((long) (idx+1))
                    .build());

            if(tagList[idx].contains("??????") && i<last_posts_id) {
                responseDtoList.add(PostsAllResponseDto.builder()
                        .authorName(tmp)
                        .postsLike(0)
                        .thumbnailName(tmp)
                        .views(0)
                        .tags(Collections.singletonList("#"+tagList[idx]))
                        .authorId(userId)
                        .postsId(postsId)
                        .build());
            }
            idx++;
        }

        String url = "http://localhost:"+port+"/api/v1/posts/"+tag+"/search/"+last_posts_id+"/recent";

        //when
        mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                //then
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDtoList)));
    }




}
