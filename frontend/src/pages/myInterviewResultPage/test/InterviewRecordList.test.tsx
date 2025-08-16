import { render, screen } from '@testing-library/react';
import InterviewRecordList from '@/pages/myInterviewResultPage/ui/InterviewRecordList';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import { interviewRecordList } from '@/entities/interviewRecord/model/mock';
import { InterviewRecordListResponse } from '@/entities/interviewRecord/model/response';

describe('InterviewRecordList', () => {
  test('InterviewRecordList 컴포넌트가 렌더링 된다.', () => {
    render(
      <MemoryRouterWrapped
        component={
          <InterviewRecordList
            interviewRecords={
              interviewRecordList as unknown as InterviewRecordListResponse[]
            }
            isLoading={false}
            onClick={() => {}}
          />
        }
      />,
    );
    expect(screen.getByLabelText('interview-record-list')).toBeInTheDocument();
  });
});
