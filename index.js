const express = require('express');
const mongoose = require('mongoose');

// Connect to MongoDB
mongoose.connect('mongodb://localhost/mydatabase', { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('Connected to MongoDB'))
  .catch(err => console.error('Failed to connect to MongoDB', err));

// Create a mongoose schema
const dataSchema = new mongoose.Schema({
  name: String,
  age: Number,
  email: String
});

// Create a mongoose model
const Data = mongoose.model('Data', dataSchema);

// Create the Express app
const app = express();
app.use(express.json());

// API endpoint to add data to MongoDB
app.post('/api/data', async (req, res) => {
  try {
    const { name, age, email } = req.body;
    const newData = new Data({ name, age, email });
    await newData.save();
    res.status(201).json(newData);
  } catch (err) {
    res.status(500).json({ error: 'Failed to add data to MongoDB' });
  }
});

// API endpoint to delete data from MongoDB
app.delete('/api/data/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const deletedData = await Data.findByIdAndDelete(id);
    if (!deletedData) {
      return res.status(404).json({ error: 'Data not found' });
    }
    res.status(200).json(deletedData);
  } catch (err) {
    res.status(500).json({ error: 'Failed to delete data from MongoDB' });
  }
});

// API endpoint to get all data from MongoDB
app.get('/api/data', async (req, res) => {
  try {
    const allData = await Data.find();
    res.status(200).json(allData);
  } catch (err) {
    res.status(500).json({ error: 'Failed to retrieve data from MongoDB' });
  }
});

const port = process.env.PORT || 3000; // Use the environment port or default to 3000

// Start the server
app.listen(port, () => {
  console.log(`Server started on port ${port}`);
});

