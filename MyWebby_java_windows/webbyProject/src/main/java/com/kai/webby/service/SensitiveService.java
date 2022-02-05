package com.kai.webby.service;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = Logger.getLogger(SensitiveService.class);

    private static final String DEFAULT_REPLACEMENT = "***";

    //内部私有类，仅供SensitiveService内部使用
    //前缀树的每个节点，所对应的类实现
    private class TrieNode {

        //关键词终结
        private boolean end = false;

        //前缀树的每个节点，后面携带的子节点集合，其中key是每个子节点的字符，value是每个子节点内部的内容
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }

        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeywordEnd(){
            return end;
        }

        void setKeywordEnd(boolean end){
            this.end = end;
        }

        public int getSubNodeCount(){
            return subNodes.size();
        }
    }

    //前缀树根节点
    private TrieNode rootNode = new TrieNode();

    //判断是否为“笑脸”等图形类符号
    private boolean isSymbol(char c){
        int ic = (int) c;
        //0x2E80-0x9FFF 东亚文字范围   isAsciiAlphanumeric:英文字符
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    //敏感词过滤算法
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();

        //指向前缀树的指针
        TrieNode tempNode = rootNode;
        //上指针
        int begin = 0;
        //下指针，活动指针，波动指针
        int position = 0;

        while(position < text.length()){
            char c = text.charAt(position);
            if(isSymbol(c)){
                if(tempNode == rootNode){
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            //当前位置的匹配结束
            if(tempNode == null){
                //以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                //回到前缀树根节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                // 发现敏感词， 从begin到position的位置用replacement替换掉
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            }else{
                ++position;
            }
        }

        //添加尾部内容
        result.append(text.substring(begin));
        return result.toString();
    }

    //根据已有敏感词，构造敏感树枝
    private void addWord(String lineTxt){
        TrieNode tempNode = rootNode;
        for(int i=0;i<lineTxt.length();++i){
            Character c = lineTxt.charAt(i);
            //过滤颜文字
            if(isSymbol(c)){
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);
            if(node == null){
                node = new TrieNode();
                tempNode.addSubNode(c,node);
            }
            tempNode = node;

            if(i == lineTxt.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        //rootNode = new TrieNode();//目测没什么用

        try{
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while((lineTxt = bufferedReader.readLine()) != null){
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        }catch(Exception e){
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }
}
