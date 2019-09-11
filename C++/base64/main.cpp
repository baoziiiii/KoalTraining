//
//  main.cpp
//  base64
//
//  Created by 包文强 on 2018/7/19.
//  Copyright © 2018年 包文强. All rights reserved.
//

#include <iostream>
#include "base64util.hpp"

using namespace std;

int main(int argc, const char * argv[]) {
    
    base64util bu;
    
    const unsigned char m[] = "Hello,world!";
    string b64;
    bu.encode(m, sizeof(m)-1,b64);
    cout << "\nbase64_crypt:\n"<<b64<<"\n";
    string r;
    bu.decode(b64,r);
    cout <<"\nraw_message:\n";
    cout << r << endl;
    
    
    const unsigned char m2[5] = {0,128,3,175,255};
    bu.encode(m2, sizeof(m2),b64);
    cout << "\nbase64_crypt:\n"<<b64<<"\n";
    bu.decode(b64,r);
    cout << "\nraw_message:\n";
    for(unsigned char c:r)
        cout<< (unsigned int)c << " ";
    cout << endl;

    return 0;
}
