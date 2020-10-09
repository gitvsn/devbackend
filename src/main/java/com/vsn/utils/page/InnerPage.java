package com.vsn.utils.page;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
public class InnerPage<T> {

    private List <T> list;

    public InnerPage(List<T> list){
        this.list = list;
    }


    @NotNull
    public PageImpl getPageInObjectList(int page, int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.Direction.DESC, "id");
        int endIndex = size > list.size() ? list.size() :  size * page;
        int startIndex = size > list.size() ? 0 : endIndex - size;

        if(page*size >= list.size()){ endIndex = list.size();}


        return new PageImpl(list.subList(startIndex,endIndex),pageRequest,list.size());
    }

}
