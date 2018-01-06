package com.sawallianc.alipay;

import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;

public class Alipay {
    public static void main(String[] args) throws Exception{
        String url = "https://openapi.alipay.com/gateway.do";
        String appid = "2018010501610600";
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCVfkcDouEZAoqEdtFpH1PnitOc21L4hFd61i025y+Og++5Q0YY7UnRpQGy6FMdC8J8UgrA9kZbhKHkZdlLyDgSJZvglh0uhkm3qUC+IEX/PA7x12+4o3XMlOCkFM68Ij7EAMHVEFiysyZkk2lsKqBzCkIZz+ifS9gtq6hjPtDx0TbDLk4qQByRcRN60VhznmGWQ4Y2LIOu6Agbs+oOELACTJ2hBjeRT6mRXvKTd0CFTt4x7L3cJOYyCRa9N5a5UcGKXRme3Y0c1qOhE4ygICmKH/0P8wSIv8kDmBJ3d63z3/yzEmjeY9q8d/PnNdQ/1m39Vibs029HabbeKghC+HY9AgMBAAECggEANOgeBeFtPMazczIB5uxZa4asYcUVVjKwKx8FnXqwUAXNn4l5k3TXPUTilwi18X+p8wmuGKCQQe9sxEJvZuyTO4jn49H8vex0xnU2HpOZcj01JM4UEgp3tFG0Nx3OFLMh94g1EUzZt1TD77BkDQ4A/vXQcwYr08Sp22/3BjjDBfFzT5T96OZ2BMXw16MNRNCSiQwbxui2i3QJxuIowSA1KCpgL743pfeeFSiCZx8ZguO9VVdxj9Px+oePqqb2XYqrOJvrZFrvIio+sPZPqW6ucCgR4IBvyecGYHXu0aA16FWkm9iERVxPyckX42i4MwM54eX9W3iahp1mQmUvEKv/2QKBgQDhTcldw+2tlDKrY1XU9yPAsW92PX6a5QGgTZ2/pmz/D5X5XNvY56XMpVidHJE1/WbZYC1xPjoi1SqvnPtP6Bo9FFH9efFS755agrMVMP5o5sSkKl/o9qZRFd5OVPndwkAQLZJxnfzeefcbHzNAZXVdfqFPgJQaTnoUNtu+F1vthwKBgQCp3FjkU3dVD1hv2De2WQdiQAioQNutGM+AHwNXBpEDTfmttoIDUNIxXwFT5rZaVLkyK7y04jMPUQUNrU7JOpcGsAVe2dI09HNevQ+DBrFZhJ+OwK80nyihdO37D7d0k/NEeP5A9dPd+OWIci1RhYfLrP+YNPkL01f+qb18XqiPGwKBgEOzaRfY4Rmwl40ymz8USFUFvFWOYvXObK+rwwQs0UHPgCRfR9yktwiIgtkrFWNg0r/tC5qktTl0TpBYnpfDSecirQR62Q4v3Kib744lm70P4vTlms5ZF170MEnfdPmy/iceWovzimGbyqoGtRLbqem/PF+0ZkyFWl+qoFs9j60PAoGBAJO994dpXdMbTNXDX/n4B+caQS1vdGNjwB/1WadZK8qPCQtiQkV2B80vkG0UsClzpb2Qs0s1sCmzU8zooC2BC5migplUpnSu5qZRlWtm590v8MRurjX79ZAxr0j/C5eXlDFLeAKyjzxz39nFTcupdErgx1PxR2lUwyXbJ9hEUSJfAoGBAN0pGPdi6o9K5rZgtepFsaytwOl+6ZDzwxabJ//gsnTgHsJFCPDkJUalzGK5+C9hMiGH2jNG1+hGMpefM2CCTF4ZvPTNLwHWItCVczTmFdX0Rb9AHyPok6KlOm7UyWX21H2rLAztWB5i2nsdxrztyREYBG9Q+nXVlgTXw3kfMofH";
        DefaultAlipayClient client = new DefaultAlipayClient(url,appid,privateKey);

        AlipayTradeWapPayRequest alipayRequest=new AlipayTradeWapPayRequest();
        alipayRequest.setBizContent("\"{\" +\n" +
                "\" \\\"out_trade_no\\\":\\\"20150320010101002\\\",\" +\n" +
                "\" \\\"total_amount\\\":\\\"88.88\\\",\" +\n" +
                "\" \\\"subject\\\":\\\"Iphone6 16G\\\",\" +\n" +
                "\" \\\"product_code\\\":\\\"QUICK_WAP_PAY\\\"\" +\n" +
                "\" }\"");
        AlipayResponse response = client.execute(alipayRequest);
        System.out.println(response);
    }
}
