package com.example.demo.pass.util;


// 把accessToken、tokenType 打包成一組JSON資料的工具 

// 長這樣:
// {"accessToken":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmaXJzdCIsImlhdCI6MTYzOTA0MDk2MywiZXhwIjoxNjM5MDQxNTY4fQ.YloKzjIBMyyYglRnkdcLJVH1xuOJHTsoiwPQDWSTAn5hLSn67NIEncE7kK8QtaKJfQC-G7y7FcqmOkgAOivKGA",
//  "tokenType":"Bearer"}

public class JWTAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public JWTAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
