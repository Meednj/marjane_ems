package com.marjane.ems.Services;

import java.util.List;
import java.util.Optional;

public interface BaseUserService<REQ, RES> {
    RES create(REQ request);
    Optional<RES> getByEmail(String email);
    Optional<RES> getByEID(String EID);
    List<RES> getAll();
    RES update(String EID, REQ request);
    void delete(String EID);
}
