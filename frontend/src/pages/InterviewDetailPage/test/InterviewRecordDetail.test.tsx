import { render, screen } from '@testing-library/react';
import InterviewRecordDetail from '@/pages/InterviewDetailPage/ui/InterviewRecordDetail';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import { interviewRecordList } from '@/entities/interviewRecord/model/mock';
import { InterviewRecordDetailResponse } from '@/entities/interviewRecord/model/response';

describe('InterviewRecordDetail', () => {
  test('InterviewRecordDetail가 렌더링 되어야 한다.', () => {
    render(
      <MemoryRouterWrapped
        component={
          <InterviewRecordDetail
            isLoading={false}
            record={
              interviewRecordList[0] as unknown as InterviewRecordDetailResponse
            }
          />
        }
      />,
    );
    expect(
      screen.getByLabelText('interview-record-detail'),
    ).toBeInTheDocument();
  });
});
