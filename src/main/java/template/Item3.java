package template;

import java.io.IOException;
import java.util.List;
import io.Message;

public class Item3 {
	public short id;
	public byte clazz;
	public byte type;
	public short level;
	public short icon;
	public byte color;
	public byte part;
	public boolean islock;
	public String name;
	public byte tier;
	public List<Option> op;
	public long time_use;

	public static int getClassDamageOptionId(byte playerClazz) {
		switch (playerClazz) {
			case 0: // Chiến Binh -> Lửa
				return 2;
			case 1: // Sát Thủ -> Độc
				return 4;
			case 2: // Pháp Sư -> Băng
				return 1;
			case 3: // Xạ Thủ -> Điện
				return 3;
			default:
				return 2;
		}
	}

	public int getParamByOptionId(int optionId) {
		if (this.op == null) {
			return 0;
		}
		int total = 0;
		for (Option o : this.op) {
			if (o != null && (o.id == optionId || Byte.toUnsignedInt(o.id) == optionId)) {
				total += o.getParam(this.tier);
			}
		}
		return total;
	}

	public void writeItemOptions(Message m, byte playerClazz) throws IOException {
		if (this.op == null || this.op.isEmpty()) {
			m.writer().writeByte(0);
			return;
		}
		int count = 0;
		for (Option o : this.op) {
			if (o != null) {
				count++;
			}
		}
		m.writer().writeByte(count);
		for (Option o : this.op) {
			if (o != null) {
				m.writer().writeByte(o.id);
				m.writer().writeInt(o.getParam(this.tier));
			}
		}
	}
}


