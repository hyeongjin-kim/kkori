import { render, screen } from '@testing-library/react';
import InterviewRecordCard from '@/pages/myInterviewResultPage/ui/InterviewRecordCard';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import { interviewRecordList } from '@/entities/interviewRecord/model/mock';

describe('InterviewRecordCard', () => {
  test('InterviewRecordCard 컴포넌트가 렌더링 된다.', () => {
    render(
      <MemoryRouterWrapped
        component={
          <InterviewRecordCard
            record={interviewRecordList[0]}
            onClick={() => {}}
          />
        }
      />,
    );
    expect(screen.getByLabelText('interview-record')).toBeInTheDocument();
  });
});
