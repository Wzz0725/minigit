package com.minigit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minigit.common.R;
import com.minigit.entity.Branch;
import com.minigit.entity.Repo;
import com.minigit.entity.User;
import com.minigit.entityService.BranchService;
import com.minigit.entityService.RepoService;
import com.minigit.entityService.UserService;
import com.minigit.service.GitService;
import com.minigit.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/{userName}")
public class RepoController {
    @Autowired
    private RepoService repoService;
    @Autowired
    private UserService userService;
    @Autowired
    private BranchService branchService;

    @Autowired
    private GitService gitService;
    /**
     *
     * @param repo
     * @param session
     * @return
     */
    @PostMapping("/init")
    public R<Repo> init(@RequestBody Repo repo, HttpSession session){
        String path = repo.getPath();
        String repoName = repo.getName();
        boolean isPublic = repo.getIsPublic();
        Long authorId = (Long) session.getAttribute("user");
        gitService.init(path, authorId, repo);
        repoService.save(repo);

        Branch branch = new Branch();
        branch.setName("main");
        branch.setRepoId(repo.getId());
        branch.setAuthorId(authorId);
        // 还没有提交，commitHash为null
        branch.setCommitHash(null);
        branchService.save(branch);
        return R.success(repo);
    }

    @GetMapping("/repos")
    public R<List<Repo>> getAllRepo(@PathVariable String userName, HttpSession session){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccountName, userName);
        User user0 = userService.getOne(queryWrapper);
        Long id = user0.getId();

        LambdaQueryWrapper<Repo> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Repo::getAuthorId, id);
        List<Repo> list = repoService.list(queryWrapper1);
        return R.success(list);
    }

    @DeleteMapping("/{repo}")
    public R<String> deleteRepo(@PathVariable String repo, HttpSession session){
        LambdaQueryWrapper<Repo> queryWrapper  = new LambdaQueryWrapper<>();
        Long authorId = (Long) session.getAttribute("user");
        queryWrapper.eq(Repo::getAuthorId, authorId).eq(Repo::getName, repo);
        Repo repo1 = repoService.getOne(queryWrapper);
        repoService.remove(queryWrapper);

        try {
            FileUtils.deleteFileOrDirectory(repo1.getPath() + File.separator + ".minigit");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success("删除成功！");
    }

    @PutMapping
    public R<Repo> updateRepo(@RequestBody Repo repo, HttpSession session){

        return null;
    }

}
