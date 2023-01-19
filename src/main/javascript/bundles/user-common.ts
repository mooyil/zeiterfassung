import "../components/avatar";
import "../components/feedback-form";
import "../components/navigation";
import "../components/time-clock";
import { initFeedbackHeartView } from "../components/feedback-heart";

const showFeedbackKudo =
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  (window as any).zeiterfassung?.feedback?.showFeedbackKudo ?? false;

initFeedbackHeartView({
  target: document.body,
  props: {
    showFeedbackKudo: showFeedbackKudo,
  },
});
