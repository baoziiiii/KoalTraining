//
//  base64util.hpp
//  base64
//
//  Created by 包文强 on 2018/7/19.
//  Copyright © 2018年 包文强. All rights reserved.
//

#ifndef base64util_hpp
#define base64util_hpp

#include <iostream>

using std::string;

struct base64util{
    
public:
    string& encode(const unsigned char*,size_t,string&);
    string& decode(const string& s,string&);
};


#endif /* base64util_hpp */
