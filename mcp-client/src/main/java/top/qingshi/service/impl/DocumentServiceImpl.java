package top.qingshi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import top.qingshi.service.DocumentService;
import top.qingshi.utils.CustomTextSplitter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService
{

    private final RedisVectorStore redisVectorStore;

    @Override
    public List<Document> loadText(Resource resource, String fileName) {

        // 加载读取文档
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("fileName", fileName);
        List<Document> documentList = textReader.get();

//        System.out.println("documentList = " + documentList);

//        默认的文本切分器
//        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
//        List<Document> list = tokenTextSplitter.apply(documentList);

        CustomTextSplitter tokenTextSplitter = new CustomTextSplitter();
        List<Document> list = tokenTextSplitter.apply(documentList);

        System.out.println("list = " + list);

        // 向量存储
        redisVectorStore.add(list);

        return documentList;
    }

    @Override
    public List<Document> doSearch(String question) {
        return redisVectorStore.similaritySearch(question);
    }
}
