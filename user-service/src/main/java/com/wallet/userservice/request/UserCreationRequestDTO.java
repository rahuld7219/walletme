package com.wallet.userservice.request;

import lombok.Data;

@Data
public class UserCreationRequestDTO {

    String name;
    String email;
    String phone;
    String kycId; // this id can represent some datasource, where all the details for this id would be stored

}
