type waitingRegistType = {
  targetUrl: string;
  maxCapacity: number;
  processingPerMinute: number;
  serviceName: string;
  queueImageUrl: string;
};

type WaitingListType = {
  id: string;
  targetUrl: string;
  maxCapacity: number;
  processingPerMinute: number;
  serviceName: string;
  queueImageUrl: string;
  partitionNo: number;
  isActive: boolean;
};

type ResponseType = {
  status: number;
  message: string;
  result: WaitingListType[];
};

export type { WaitingListType, ResponseType, waitingRegistType };
