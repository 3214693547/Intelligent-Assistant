package top.qingshi.service;


import top.qingshi.bean.SearchResult;

import java.util.List;

public interface SesrXngService
{

    /**
     * @Description: 调用本地搜索引擎searxng进行搜索
     * @return List<SearchResult>
     */
    public List<SearchResult> search(String query);

}
