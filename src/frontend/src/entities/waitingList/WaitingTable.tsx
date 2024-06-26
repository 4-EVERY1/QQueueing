"use client";
import React from "react";
import { WaitingListType } from "./type";
import { useRouter } from "next/navigation";

type waitingListType = {
  waitingList: WaitingListType[];
};

const WaitingTable = ({ waitingList }: waitingListType) => {
  const router = useRouter();
  return (
    <table className="flex flex-col flex-1 rounded-md shadow-lg border">
      <thead>
        <tr className="flex items-center w-full text-center border-b border-slate-300 text-[1.5rem] font-bold h-[60px]">
          <th className="w-[20%]">대기열 번호</th>
          <th className="w-[35%]">등록 URL</th>
          <th className="w-[30%]">서비스 명</th>
          <th className="w-[15%]">활성 여부</th>
        </tr>
      </thead>
      <tbody>
        {waitingList && waitingList.length > 0 ? (
          waitingList.map((item, index) => (
            <tr
              key={item.id}
              className="flex items-center w-full text-center border-b border-black h-[60px] cursor-pointer"
              onClick={() => {
                router.push(`/manage/${item.id}`);
              }}
            >
              <td className="w-[20%]">{index + 1}</td>
              <td className="w-[35%]">{item.targetUrl}</td>
              <td className="w-[30%]">{item.serviceName}</td>
              <td className="w-[15%]">
                {item.isActive ? "활성 중" : "비 활성"}
              </td>
            </tr>
          ))
        ) : (
          <tr className="flex w-full h-full items-center justify-center text-center self-center">
            <td>
              <p>등록한 데이터가 없습니다</p>
            </td>
          </tr>
        )}
      </tbody>
    </table>
  );
};

export default WaitingTable;
