package com.sun.tingle.mission.service;

import com.sun.tingle.calendar.db.entity.CalendarEntity;
import com.sun.tingle.mission.db.entity.MissionEntity;
import com.sun.tingle.mission.db.repo.MissionRepository;
import com.sun.tingle.mission.requestdto.MissionRqDto;
import com.sun.tingle.mission.responsedto.MissionRpDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MissionServiceImpl implements MissionService {
    @Autowired
    MissionRepository missionRepository;



    @Override
    public MissionRpDto insertMission(MissionRqDto missionRqDto) {
        MissionEntity missionEntity = missionRepository.findByTitle(missionRqDto.getTitle());
        if(missionEntity != null) { // 이미 같은 미션 이름 있을 때
            return null;
        }

        missionEntity = new MissionEntity();
        missionEntity.setTitle(missionRqDto.getTitle());
        missionEntity.setStart(missionRqDto.getStart());
        missionEntity.setEnd(missionRqDto.getEnd());
        missionEntity.setCalendarCode(missionRqDto.getCalendarCode());
        missionEntity.setId(missionRqDto.getId());
        List<String> list = missionRqDto.getTag();
        StringBuilder sb = new StringBuilder();
        int size = list.size();

        for(int i=0; i<size; i++) {
            sb.append("#").append(list.get(i));
        }
        missionEntity.setTag(sb.toString());
        missionEntity = missionRepository.save(missionEntity);

        MissionRpDto missionRpDto = new MissionRpDto();
        String[] tagArr = missionEntity.getTag().split("#");
        list = new ArrayList<>();
        size = tagArr.length;
        for(int i=1; i<size; i++) {
            list.add(tagArr[i]);
        }

        missionRpDto = missionRpDto.builder().missionId(missionEntity.getMissionId())
                .tag(list).title(missionEntity.getTitle())
                .calendarCode(missionEntity.getCalendarCode())
                .start(missionEntity.getStart())
                .end(missionEntity.getEnd())
                .id(missionEntity.getId())
                .build();

        return missionRpDto;
    }

    @Override
    public MissionRpDto selectMission(Long missionId) {
        MissionEntity missionEntity = missionRepository.findByMissionId(missionId);
        MissionRpDto missionRpDto = new MissionRpDto();
        String[] tagArr = missionEntity.getTag().split("#");
        List<String> list = new ArrayList<>();
        int size = tagArr.length;
        for(int i=1; i<size; i++) {
            list.add(tagArr[i]);
        }

        missionRpDto = missionRpDto.builder().missionId(missionEntity.getMissionId())
                .tag(list).title(missionEntity.getTitle())
                .calendarCode(missionEntity.getCalendarCode())
                .start(missionEntity.getStart())
                .end(missionEntity.getEnd())
                .id(missionEntity.getId())
                .build();




        return missionRpDto;
    }

    @Override
    public MissionRpDto updateMission(Long missionId,MissionRqDto missionRqDto) {
        MissionEntity missionEntity = missionRepository.findByMissionId(missionId);
        if(missionEntity == null) {
            return null;
        }

        List<String> list = missionRqDto.getTag();
        int size = list.size();
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<size; i++) {
            sb.append("#").append(list.get(i));
        }
        missionEntity = new MissionEntity();

        missionEntity = missionEntity.builder().missionId(missionId).title(missionRqDto.getTitle())
                .start(missionRqDto.getStart())
                .end(missionRqDto.getEnd())
                .tag(sb.toString()).calendarCode(missionRqDto.getCalendarCode())
                .id(missionRqDto.getId())
                .build();

        missionEntity = missionRepository.save(missionEntity);

        MissionRpDto missionRpDto = new MissionRpDto();
        missionRpDto = missionRpDto.builder().missionId(missionEntity.getMissionId())
                .title(missionEntity.getTitle())
                .start(missionEntity.getStart())
                .end(missionEntity.getEnd())
                .tag(missionRqDto.getTag()).
                id(missionEntity.getId()).
                calendarCode(missionEntity.getCalendarCode()).build();

        return missionRpDto;
    }

    @Override
    public void deleteMission(Long missionId) {
        missionRepository.deleteById(missionId);
    }

    @Override
    public List<MissionRpDto> selectMissionList(String calendarCode) {
        List<MissionEntity> list = missionRepository.findByCalendarCode(calendarCode);
        if(list == null) {
            return null;
        }
        List<MissionRpDto> list2 = builderMissionList(list);

        return list2;
    }


    public List<MissionRpDto> builderMissionList(List<MissionEntity> list) {
        List<MissionRpDto> list2 = new ArrayList<>();
        MissionEntity m = new MissionEntity();
        MissionRpDto missionRpDto = new MissionRpDto();
        int size = list.size();
        List<String> tags = null;
        for(int i=0; i<size; i++) {
            m = list.get(i);
            String[] temp = m.getTag().split("#");
            tags = new ArrayList<>();
            for(int j=0;j<temp.length;j++) {
                tags.add(temp[j]);
            }
            missionRpDto = missionRpDto.builder().calendarCode(m.getCalendarCode())
                    .missionId(m.getMissionId())
                    .title(m.getTitle())
                    .end(m.getEnd())
                    .start(m.getStart())
                    .tag(tags)
                    .id(m.getId())
                    .build();
            list2.add(missionRpDto);
        }

        return list2;
    }
}
