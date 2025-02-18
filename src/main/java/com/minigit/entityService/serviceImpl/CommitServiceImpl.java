package com.minigit.entityService.serviceImpl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minigit.entity.Commit;
import com.minigit.mapper.CommitMapper;
import com.minigit.entityService.CommitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CommitServiceImpl extends ServiceImpl<CommitMapper, Commit> implements CommitService {

}
