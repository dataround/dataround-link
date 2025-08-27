/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * @description: cron expression editor
 * @author: yuehan124@gmail.com
 * @date: 2026-06-05
 */
import { Form, Row, Select, TimePicker } from "antd";
import dayjs from "dayjs";
import type { Dayjs } from 'dayjs';
import type { TimePickerProps } from 'antd';
import { FC, memo, useEffect, useState } from "react";
import { CRON_PER_DAY, CRON_PER_HOUR, CRON_PER_MIN, CRON_PER_MONTH, CRON_PER_WEEK } from "../store";
import { useTranslation } from 'react-i18next';

interface IProps {
    cron: string;
    onChange?: (cron: string) => void;
}

const parseCronExpression = (cron: string) => {
    console.log("cron: " + cron);
    const initialValue = { "frequency": CRON_PER_DAY, "second": ["0"], "minute": ["0"], "week": ["MON"], "day": ["1"], "time": dayjs() };
    if (!cron) {
        return initialValue;
    }
    while (cron.indexOf(", ") !== -1) {
        cron = cron.replace(", ", ",");
    }
    // * * * * * ? => second minute hour day month week    
    const array: string[] = cron.split(" ");
    const week: string = array[5];
    const day: string = array[3];
    const hour: string = array[2];
    const minute: string = array[1];
    const second: string = array[0];
    if (week !== "?") {
        return { ...initialValue, "frequency": CRON_PER_WEEK, "week": week.split(","), "time": dayjs(array[2] + array[1] + array[0], "HHmmss") };
    }
    if (day !== "*") {
        return { ...initialValue, "frequency": CRON_PER_MONTH, "day": day.split(","), "time": dayjs(array[2] + array[1] + array[0], "HHmmss") };
    }
    if (hour !== "*") {
        return { ...initialValue, "frequency": CRON_PER_DAY, "time": dayjs(array[2] + array[1] + array[0], "HHmmss") };
    }
    if (minute !== "*") {
        return { ...initialValue, "frequency": CRON_PER_HOUR, "minute": minute.split(","), "second": second.split(",") };
    }
    if (array[0] !== "*") {
        return { ...initialValue, "frequency": CRON_PER_MIN, "second": second.split(",") };
    }
    return initialValue;
}

const C: FC<IProps> = ({ cron, onChange }) => {
    const { t } = useTranslation();
    // convert cron string to form values
    const initialValue = parseCronExpression(cron);
    const [cronFrequency, setCronFrequency] = useState<string>(initialValue.frequency);
    const [cronSecondSelect, setCronSecondSelect] = useState<string[]>(initialValue.second);
    const [cronMinuteSelect, setCronMinuteSelect] = useState<string[]>(initialValue.minute);
    const [cronWeekSelect, setCronWeekSelect] = useState<string[]>(initialValue.week);
    const [cronDaySelect, setCronDaySelect] = useState<string[]>(initialValue.day);
    const [cronTime, setCronTime] = useState<Dayjs>(initialValue.time);

    const onCronFrequencyChange = (value: string) => {
        setCronFrequency(value);
    };

    const onCronSecondSelectChange = (value: string[]) => {
        setCronSecondSelect(value);
    }

    const onCronMinuteSelectChange = (value: string[]) => {
        setCronMinuteSelect(value);
    }

    const onCronWeekSelectChange = (value: string[]) => {
        setCronWeekSelect(value);
    }

    const onCronDaySelectChange = (value: string[]) => {
        setCronDaySelect(value);
    }

    const onCronTimeChange = (time: Dayjs | null, dateString: string) => {
        if (time) {
            setCronTime(time);
        }
    }

    useEffect(() => {
        buildCronExpression();
    }, [cronFrequency, cronSecondSelect, cronMinuteSelect, cronWeekSelect, cronDaySelect, cronTime]);

    const buildCronExpression = () => {
        const array: string[] = ["*", "*", "*", "*", "*", "?"];
        if (cronFrequency === CRON_PER_MIN) {
            array[0] = cronSecondSelect.join(",");
        } else if (cronFrequency === CRON_PER_HOUR) {
            array[0] = "0";
            array[1] = cronMinuteSelect.join(",");
        } else if (cronFrequency === CRON_PER_DAY) {
            array[0] = cronTime.format('ss');
            array[1] = cronTime.format('mm');
            array[2] = cronTime.format('HH');
        } else if (cronFrequency === CRON_PER_WEEK) {
            array[0] = cronTime.format('ss');
            array[1] = cronTime.format('mm');
            array[2] = cronTime.format('HH');
            array[5] = cronWeekSelect.join(",");
        } else if (cronFrequency === CRON_PER_MONTH) {
            array[0] = cronTime.format('ss');
            array[1] = cronTime.format('mm');
            array[2] = cronTime.format('HH');
            array[3] = cronDaySelect.join(",");
        }
        const cronExpression = array.join(" ");
        // call the onChange callback
        if (onChange) {
            onChange(cronExpression);
        }
    }

    const cronFrequencyOptions = [
        { label: t('job.edit.cron.frequency.perMinute'), value: CRON_PER_MIN },
        { label: t('job.edit.cron.frequency.perHour'), value: CRON_PER_HOUR },
        { label: t('job.edit.cron.frequency.perDay'), value: CRON_PER_DAY },
        { label: t('job.edit.cron.frequency.perWeek'), value: CRON_PER_WEEK },
        { label: t('job.edit.cron.frequency.perMonth'), value: CRON_PER_MONTH }
    ];

    const secondOptions = () => {
        return Array.from({ length: 60 }, (_, i) => i).map((i) => ({
            label: `${i}`,
            value: `${i}`,
        }));
    };

    const minuteOptions = () => {
        return Array.from({ length: 60 }, (_, i) => i).map((i) => ({
            label: `${i}`,
            value: `${i}`,
        }));
    };

    const weekOptions = () => {
        return [
            { label: t('job.edit.cron.weekday.monday'), value: 'MON' },
            { label: t('job.edit.cron.weekday.tuesday'), value: 'TUE' },
            { label: t('job.edit.cron.weekday.wednesday'), value: 'WED' },
            { label: t('job.edit.cron.weekday.thursday'), value: 'THU' },
            { label: t('job.edit.cron.weekday.friday'), value: 'FRI' },
            { label: t('job.edit.cron.weekday.saturday'), value: 'SAT' },
            { label: t('job.edit.cron.weekday.sunday'), value: 'SUN' },
        ]
    };

    const dayOptions = () => {
        return Array.from({ length: 31 }, (_, i) => i + 1).map((i) => ({
            label: `${i}`,
            value: `${i}`,
        }));
    };

    return (
        <Form.Item name="cron" label={t('job.edit.cron.form.frequency')} rules={[{ required: true, message: t('job.edit.cron.form.frequencyRequired') }]}>
            <Row>
                <Form.Item initialValue={cronFrequency} style={{ marginBottom: 0 }}>
                    <Select 
                        placeholder={t('job.edit.cron.placeholder.select')} 
                        style={{ width: 100 }} 
                        value={cronFrequency} 
                        options={cronFrequencyOptions} 
                        onChange={onCronFrequencyChange}
                    />
                    &nbsp;
                </Form.Item>
                {cronFrequency === CRON_PER_MIN && (
                    <Form.Item initialValue={cronSecondSelect} style={{ marginBottom: 0 }}>
                        <Select 
                            mode="multiple" 
                            style={{ width: 180 }} 
                            value={cronSecondSelect} 
                            options={secondOptions()} 
                            onChange={onCronSecondSelectChange}
                        />
                        &nbsp;{t('job.edit.cron.unit.second')}
                    </Form.Item>
                )}
                {cronFrequency === CRON_PER_HOUR && (
                    <Form.Item initialValue={cronMinuteSelect} style={{ marginBottom: 0 }}>
                        <Select 
                            mode="multiple" 
                            style={{ width: 180 }} 
                            value={cronMinuteSelect} 
                            options={minuteOptions()} 
                            onChange={onCronMinuteSelectChange}
                        />
                        &nbsp;{t('job.edit.cron.unit.minute')}
                    </Form.Item>
                )}
                {cronFrequency === CRON_PER_DAY && (
                    <Form.Item initialValue={cronTime} style={{ marginBottom: 0 }}>
                        <TimePicker 
                            style={{ width: 150 }} 
                            value={cronTime} 
                            onChange={onCronTimeChange as any}
                        />
                        &nbsp;{t('job.edit.cron.unit.hour')}
                    </Form.Item>
                )}
                {cronFrequency === CRON_PER_WEEK && (
                    <Form.Item initialValue={cronWeekSelect} style={{ marginBottom: 0 }}>
                        <Select 
                            mode="multiple" 
                            style={{ width: 180 }} 
                            value={cronWeekSelect} 
                            options={weekOptions()} 
                            onChange={onCronWeekSelectChange}
                        />
                        &nbsp;{t('job.edit.cron.unit.day')}
                        <TimePicker 
                            value={cronTime} 
                            onChange={onCronTimeChange as any}
                        />
                        &nbsp;{t('job.edit.cron.unit.hour')}
                    </Form.Item>
                )}
                {cronFrequency === CRON_PER_MONTH && (
                    <Form.Item initialValue={cronDaySelect} style={{ marginBottom: 0 }}>
                        <Select 
                            mode="multiple" 
                            style={{ width: 180 }} 
                            value={cronDaySelect} 
                            options={dayOptions()} 
                            onChange={onCronDaySelectChange}
                        />
                        &nbsp;{t('job.edit.cron.unit.day')}
                        <TimePicker 
                            value={cronTime} 
                            onChange={onCronTimeChange as any}
                        />
                        &nbsp;{t('job.edit.cron.unit.hour')}
                    </Form.Item>
                )}
            </Row>
        </Form.Item>
    );
};

const CronExpression = memo(C);

export default CronExpression;