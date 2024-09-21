package vn.tayjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.tayjava.configuration.Translator;
import vn.tayjava.dto.request.UserRequestDTO;
import vn.tayjava.dto.response.PageResponse;
import vn.tayjava.dto.response.ResponseData;
import vn.tayjava.dto.response.ResponseError;
import vn.tayjava.dto.response.UserDetailResponse;
import vn.tayjava.model.User;
import vn.tayjava.repository.SearchRepository;
import vn.tayjava.service.UserService;
import vn.tayjava.util.UserStatus;

import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SearchRepository searchRepository;

    private static final String ERROR_MESSAGE = "errorMessage={}";

    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping(value = "/")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO request) {
        log.info("Request add user, {} {}", request.getFirstName(), request.getLastName());

        try {
            long userId = userService.saveUser(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add user fail");
        }
    }

    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{userId}")
    public ResponseData<Void> updateUser(@PathVariable @Min(1) long userId, @Valid @RequestBody UserRequestDTO request) {
        log.info("Request update userId={}", userId);

        try {
            userService.updateUser(userId, request);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
        }
    }

    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{userId}")
    public ResponseData<Void> updateStatus(@Min(1) @PathVariable int userId, @RequestParam UserStatus status) {
        log.info("Request change status, userId={}", userId);

        try {
            userService.changeStatus(userId, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change status fail");
        }
    }

    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{userId}")
    public ResponseData<Void> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") long userId) {
        log.info("Request delete userId={}", userId);

        try {
            userService.deleteUser(userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.del.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete user fail");
        }
    }

    @Operation(summary = "Get user detail", description = "Send a request via this API to get user information")
    @GetMapping("/{userId}")
    public ResponseData<User> getUser(@PathVariable @Min(1) long userId) {
        log.info("Request get user detail, userId={}", userId);

        try {
            User user = userService.getUser(userId);
            return new ResponseData<>(HttpStatus.OK.value(), "user", user);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<PageResponse> getAllUsers(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                  @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                  @RequestParam(required = false) String softBy) {
        log.info("Request get user list, pageNo={}, pageSize={}", pageNo, pageSize);
        try {
            PageResponse<?> users = userService.getAllUsers(pageNo, pageSize, softBy);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list-multi-sorts")
    public ResponseData<PageResponse> getAllUsersWithMultiSorts(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                                @RequestParam(required = false) String... softBy) {
        log.info("Request get user list, pageNo={}, pageSize={}", pageNo, pageSize);
        try {
            PageResponse<?> users = userService.getAllUsersWithMultiSortColumns(pageNo, pageSize, softBy);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/search")
    public ResponseData<PageResponse> getAllUsers(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                  @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                  @RequestParam(required = false) String search,
                                                  @RequestParam(required = false) String softBy) {
        log.info("Request get user list, pageNo={}, pageSize={}", pageNo, pageSize);
        try {
            PageResponse<?> users = searchRepository.getAllUserSWithSoftByColumnAndSearch(pageNo, pageSize, search, softBy);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/advance-search")
    public ResponseData<PageResponse> getAllUsersAdvanceSearch(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                               @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                               @RequestParam(required = false) String softBy,
                                                               @RequestParam(required = false, defaultValue = "") String... search) {
        log.info("Request get user list, pageNo={}, pageSize={}", pageNo, pageSize);
        try {
            PageResponse<?> users = userService.advanceSearchByCriteria(pageNo, pageSize, softBy, search);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @GetMapping("/get-list-user")
    public List<User> getAllUsersBySorting(@RequestParam String sortType){
        return userService.getAllUsersBySorting(sortType);
    }
}
