package com.pluto.config;

public class Config {
    //
    public static String Version = "1.0.2";
    //
    public static String Code = "2";
    //Gitbook钱包说明地址
    public static final String GitbookUrl = "https://tapfantasy.gitbook.io/plutowallet/";
    //
    public static String DebugWalletUrl = "https://wal-sandbox.pluto.vision/#/";
    //
    public static String WalletUrl = "https://wal.pluto.vision/#/";
    //
    public static final String AuthUrl = "https://auth.pluto.vision";
    //
    public static final String DebugAuthUrl = "https://auth-sandbox.pluto.vision";
    //
    public static final String RankUrl = "https://authrank.pluto.vision";
    //
    public static final String DebugRankUrl = "https://authrank-sandbox.pluto.vision";
    //token登录地址
    public static final String LoginWithTokenPath = "/api/account/loginwithtoken";
    //第三方登录地址
    public static final String LoginWithPlatformPath = "/api/account/pflogin";
    //email登录地址
    public static final String LoginWithEmailPath = "/api/account/login";
    //请求验证码地址
    public static final String ReqCodePath = "/api/account/seccode";
    //请求钱包数据地址
    public static final String ReqWalletInfoPath = "/api/account/walletinfo";
    //预请求小鱼代币兑换ETH地址
    public static final String PreFishSwapEthPath = "/api/account/preswap";
    //小鱼代币兑换ETH地址
    public static final String FishSwapEthPath = "/api/account/swap";
    //ETH提现地址
    public static final String EthWithdrawPath = "/api/account/withdraw";
    //提现记录地址
    public static final String WithdrawLogPath = "/api/account/withdrawlog";
    //上传保存积分排行
    public static final String SaveScoreRankPath = "/api/rank/savescore";
    //拉取积分排行
    public static final String ReqScoreRankPath = "/api/rank/getscoreranks";
}
