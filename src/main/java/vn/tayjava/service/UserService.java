package vn.tayjava.service;

import vn.tayjava.dto.request.UserRequestDTO;
import vn.tayjava.dto.response.PageResponse;
import vn.tayjava.dto.response.ResponseData;
import vn.tayjava.dto.response.UserDetailResponse;
import vn.tayjava.model.User;
import vn.tayjava.util.UserStatus;

import java.util.List;

public interface UserService {

    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    User getUser(long userId);

    PageResponse getAllUsers(int pageNo, int pageSize, String sortBy);

    PageResponse getAllUsersWithMultiSortColumns(int pageNo, int pageSize, String... sortBy);

    PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String softBy, String... search);

    List<User> getAllUsersBySorting(String sortType);
}
