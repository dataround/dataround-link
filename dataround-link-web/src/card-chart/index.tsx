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
 * @description: chart component
 * @author: yuehan124@gmail.com
 * @date: 2026-06-05
 */
import { FC, memo, ReactNode } from "react";

import "./index.less";
import { Card } from "antd";

interface IProps {
  title: string;
  value: string;
}

const C: FC<IProps> = (props) => {
  return (
    <Card hoverable>
      <div>
        <div className="panel-title">{props.title}</div>
        <div className="panel-content">
          <span style={{ fontSize: "45px" }}>{props.value}</span>
          <span style={{ fontSize: "28px" }}>%</span>
        </div>
      </div>
    </Card>
  );
};

const CardChart = memo(C);

export default CardChart;
