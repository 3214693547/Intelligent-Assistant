package top.qingshi.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.qingshi.bean.ChatEntity;
import top.qingshi.service.ChatService;
import top.qingshi.service.DocumentService;
import top.qingshi.utils.LeeResult;

import java.util.List;

/**
 * @ClassName HelloController
 * @Author 风间影月
 * @Version 1.0
 * @Description HelloController
 **/
@RestController
@RequestMapping("rag")
public class RagController
{

    @Resource
    private DocumentService documentService;

    @Resource
    private ChatService chatService;

    @PostMapping("/uploadRagDoc")
    public LeeResult uploadRagDoc(@RequestParam("file") MultipartFile file ){
        List<Document> documentList =  documentService.
                loadText(file.getResource(), file.getOriginalFilename());
        return LeeResult.ok(documentList);
    }

    @GetMapping("/doSearch")
    public LeeResult doSearch(@RequestParam String question) {
        return LeeResult.ok(documentService.doSearch(question));
    }

    @PostMapping("/search")
    public void search(@RequestBody ChatEntity chatEntity, HttpServletRequest request, HttpServletResponse response) {

        List<Document> list = documentService.doSearch(chatEntity.getMessage());
        response.setCharacterEncoding("UTF-8");
        
        // 从请求中获取用户ID（由JWT拦截器注入）
        Long userId = (Long) request.getAttribute("userId");
        chatService.doChatRagSearch(chatEntity, list, userId);
    }

}
