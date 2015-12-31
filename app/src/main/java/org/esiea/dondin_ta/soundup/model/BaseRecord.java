package org.esiea.dondin_ta.soundup.model;

import java.io.File;
import java.util.Date;

public interface BaseRecord {
    public String getRecordTime();

    public void startRecord();

    public void stopRecord();

    public void onPause();

    public String getName();

    public void setName(String name);

    public void setRecordFile(File recordFile);

    public void setCreateTime(Date createTime);
    public Date getCreateTime();

    public long getLength();
    public void setLength(long length);

}
