//
//  base64util.cpp
//  base64
//
//  Created by 包文强 on 2018/7/19.
//  Copyright © 2018年 包文强. All rights reserved.
//

#include "base64util.hpp"
#include <iostream>

using std::string;

private:
    //加密表
    string encode_table {"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"};
    //解码表
    const unsigned char decode_table[172] =
    {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        62, // '+'
        0, 0, 0,
        63, // '/'
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // '0'-'9'
        0, 0, 0, 0, 0, 0, 0,
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
        13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // 'A'-'Z'
        0, 0, 0, 0, 0, 0,
        26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // 'a'-'z'
    };

string& base64util::encode(const unsigned char* data,size_t size,string& r){

    const unsigned char * p=data;
    auto &et=encode_table;
    char t[4];
    r.clear();
    
    size_t loop=size/3;
    while(loop--){
        t[0] = et[(p[0] >> 2)];
        t[1] = et[((p[0] & 0x03) << 4) + (p[1] >> 4)];
        t[2] = et[((p[1] & 0x0F) << 2) + (p[2] >> 6)];
        t[3] = et[(p[2] & 0x3F)];
        r += string(t,4);
        p+=3;
    }
    if(size%3==1){
        t[0] = et[(p[0] >> 2)];
        t[1] = et[((p[0] & 0x03) << 4)];
        r += string(t,2) + "==";
    }else if(size%3==2){
        t[0] = et[(p[0] >> 2)];
        t[1] = et[((p[0] & 0x03) << 4) + (p[1] >> 4)];
        t[2] = et[((p[1] & 0x0F) << 2)];
        r += string(t,3) + "=";
    }
    return r;
}

string& base64util::decode(const string& s,string& r){
    
    char t[3];
    auto &dt=decode_table;
    r.clear();
    
    size_t size=s.size();
    for(int i=0;i<s.size();i+=4){
        t[0] = (dt[s[i]] << 2)+(dt[s[i+1]] >> 4);
        t[1] = (dt[s[i+1]] << 4)+(dt[s[i+2]] >> 2);
        t[2] = (dt[s[i+2]] << 6)+dt[s[i+3]];
        r+=string(t,3);
    }
    
    if(s[size-1]=='='){
        r.pop_back();
    }
    if(s[size-2]=='='){
        r.pop_back();
    }
    return r;
}

