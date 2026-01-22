package top.qingshi.service;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DocumentService
{

    /**
     * @Description: 加载文档并且读取数据进行保存到知识库
     * @param resource
     * @param fileName
     */
    public List<Document> loadText(Resource resource, String fileName);

    /**
     * @Description: 根据提问从知识库中查询相应的知识/资料（相似）
     * @param question
     * @return List<Document>
     */
    public List<Document> doSearch(String question);

}
