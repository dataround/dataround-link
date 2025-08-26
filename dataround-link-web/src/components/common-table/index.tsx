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
 * CommonTable component - Simple and clean
 * @author: yuehan124@gmail.com
 * @date: 2025-01-08
 */
import { Table, TableProps } from 'antd';
import { FC, memo } from 'react';

interface CommonTableProps<T = any> extends Omit<TableProps<T>, 'size'> {
    /**
     * show border or not, default false
     */
    bordered?: boolean;
    /**
     * table size, default small
     */
    size?: 'small' | 'middle' | 'large';
    /**
     * enable pagination, default true
     */
    enablePagination?: boolean;
    /**
     * page size, default 10
     */
    pageSize?: number;
    /**
     * total count
     */
    total?: number;
    /**
     * page change callback
     */
    onPageChange?: (current: number, size: number) => void;
}

const T: FC<CommonTableProps> = ({
    bordered = false,
    size = 'small',
    enablePagination = true,
    pageSize = 10,
    total,
    onPageChange,
    pagination,
    ...tableProps
}) => {
    // default pagination config
    const defaultPagination = enablePagination ? {
        pageSize,
        total,
        onChange: onPageChange,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total: number, range: [number, number]) =>
            `Page ${range[0]}-${range[1]} of ${total}`,
        pageSizeOptions: ['10', '20', '50', '100'],
    } : false;

    return (
        <Table
            bordered={bordered}
            size={size}
            pagination={defaultPagination}
            rowKey={(record: any, index?: number) => record.key || record.id || index}
            {...tableProps}
        />
    );
};

const CommonTable = memo(T);

export default CommonTable; 