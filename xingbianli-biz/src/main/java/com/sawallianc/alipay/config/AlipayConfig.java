package com.sawallianc.alipay.config;

public class AlipayConfig {
    // 商户appid
    public static String APPID = "2018010501610600";
    // 私钥 pkcs8格式的
    public static String RSA_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCVfkcDouEZAoqEdtFpH1PnitOc21L4hFd61i025y+Og++5Q0YY7UnRpQGy6FMdC8J8UgrA9kZbhKHkZdlLyDgSJZvglh0uhkm3qUC+IEX/PA7x12+4o3XMlOCkFM68Ij7EAMHVEFiysyZkk2lsKqBzCkIZz+ifS9gtq6hjPtDx0TbDLk4qQByRcRN60VhznmGWQ4Y2LIOu6Agbs+oOELACTJ2hBjeRT6mRXvKTd0CFTt4x7L3cJOYyCRa9N5a5UcGKXRme3Y0c1qOhE4ygICmKH/0P8wSIv8kDmBJ3d63z3/yzEmjeY9q8d/PnNdQ/1m39Vibs029HabbeKghC+HY9AgMBAAECggEANOgeBeFtPMazczIB5uxZa4asYcUVVjKwKx8FnXqwUAXNn4l5k3TXPUTilwi18X+p8wmuGKCQQe9sxEJvZuyTO4jn49H8vex0xnU2HpOZcj01JM4UEgp3tFG0Nx3OFLMh94g1EUzZt1TD77BkDQ4A/vXQcwYr08Sp22/3BjjDBfFzT5T96OZ2BMXw16MNRNCSiQwbxui2i3QJxuIowSA1KCpgL743pfeeFSiCZx8ZguO9VVdxj9Px+oePqqb2XYqrOJvrZFrvIio+sPZPqW6ucCgR4IBvyecGYHXu0aA16FWkm9iERVxPyckX42i4MwM54eX9W3iahp1mQmUvEKv/2QKBgQDhTcldw+2tlDKrY1XU9yPAsW92PX6a5QGgTZ2/pmz/D5X5XNvY56XMpVidHJE1/WbZYC1xPjoi1SqvnPtP6Bo9FFH9efFS755agrMVMP5o5sSkKl/o9qZRFd5OVPndwkAQLZJxnfzeefcbHzNAZXVdfqFPgJQaTnoUNtu+F1vthwKBgQCp3FjkU3dVD1hv2De2WQdiQAioQNutGM+AHwNXBpEDTfmttoIDUNIxXwFT5rZaVLkyK7y04jMPUQUNrU7JOpcGsAVe2dI09HNevQ+DBrFZhJ+OwK80nyihdO37D7d0k/NEeP5A9dPd+OWIci1RhYfLrP+YNPkL01f+qb18XqiPGwKBgEOzaRfY4Rmwl40ymz8USFUFvFWOYvXObK+rwwQs0UHPgCRfR9yktwiIgtkrFWNg0r/tC5qktTl0TpBYnpfDSecirQR62Q4v3Kib744lm70P4vTlms5ZF170MEnfdPmy/iceWovzimGbyqoGtRLbqem/PF+0ZkyFWl+qoFs9j60PAoGBAJO994dpXdMbTNXDX/n4B+caQS1vdGNjwB/1WadZK8qPCQtiQkV2B80vkG0UsClzpb2Qs0s1sCmzU8zooC2BC5migplUpnSu5qZRlWtm590v8MRurjX79ZAxr0j/C5eXlDFLeAKyjzxz39nFTcupdErgx1PxR2lUwyXbJ9hEUSJfAoGBAN0pGPdi6o9K5rZgtepFsaytwOl+6ZDzwxabJ//gsnTgHsJFCPDkJUalzGK5+C9hMiGH2jNG1+hGMpefM2CCTF4ZvPTNLwHWItCVczTmFdX0Rb9AHyPok6KlOm7UyWX21H2rLAztWB5i2nsdxrztyREYBG9Q+nXVlgTXw3kfMofH";
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/notify_url.jsp";
    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    public static String return_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/return_url.jsp";
    // 请求网关地址
    public static String URL = "https://openapi.alipay.com/gateway.do";
    // 编码
    public static String CHARSET = "UTF-8";
    // 返回格式
    public static String FORMAT = "json";
    // 支付宝公钥
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjrEVFMOSiNJXaRNKicQuQdsREraftDA9Tua3WNZwcpeXeh8Wrt+V9JilLqSa7N7sVqwpvv8zWChgXhX/A96hEg97Oxe6GKUmzaZRNh0cZZ88vpkn5tlgL4mH/dhSr3Ip00kvM4rHq9PwuT4k7z1DpZAf1eghK8Q5BgxL88d0X07m9X96Ijd0yMkXArzD7jg+noqfbztEKoH3kPMRJC2w4ByVdweWUT2PwrlATpZZtYLmtDvUKG/sOkNAIKEMg3Rut1oKWpjyYanzDgS7Cg3awr1KPTl9rHCazk15aNYowmYtVabKwbGVToCAGK+qQ1gT3ELhkGnf3+h53fukNqRH+wIDAQAB";
    // 日志记录目录
    public static String log_path = "/log";
    // RSA2
    public static String SIGNTYPE = "RSA2";
}
